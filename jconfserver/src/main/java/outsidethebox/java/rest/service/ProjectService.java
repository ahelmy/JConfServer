/**
 * 
 */
package outsidethebox.java.rest.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import outsidethebox.java.backend.JPAHandler;
import outsidethebox.java.hibernate.models.Project;
import outsidethebox.java.rest.model.ConfResponse;
import outsidethebox.java.servers.ManageServers;
import outsidethebox.java.servers.pojo.Server;
import outsidethebox.java.utils.Utils;

/**
 * @author AliHelmy
 *
 */
@Path("/Project")
public class ProjectService {

	public static final Project POST_PROJECT_SAMPLE = new Project();
	static {
		POST_PROJECT_SAMPLE.setName("some_name");
	}

	@Context
	HttpServletRequest request;

	@GET
	@Path("/{project}")
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
	public Response getproject(@PathParam("project") String project, @QueryParam("details") Boolean details) {
		Project proj = JPAHandler.getInstance().getProject(project);
		if (proj != null) {
			if (details != null && details) {
				return Response.ok().entity(proj.toDetailedString()).build();
			} else {
				return Response.ok().entity(proj.toString()).build();
			}
		}
		return Response.status(Status.NOT_FOUND)
				.entity(new ConfResponse<>("KEY [" + project + "] not found").toString()).build();
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getproject() {
		List<Project> projects = JPAHandler.getInstance().getAllProjects();
		if (projects != null && !projects.isEmpty()) {
			JsonArray array = new JsonArray();
			projects.stream().forEach(proj -> array.add(proj.toDetailedJSON()));
			return Response.ok().entity(new GsonBuilder().setPrettyPrinting().create().toJson(array)).build();
		}
		return Response.status(Status.NOT_FOUND).entity(new ConfResponse<>("No Projects").toString()).build();

	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postPackage(Project project, @QueryParam("StopSync") Boolean stopSync) {
		if (project != null && project.validPOST()) {
			JPAHandler.getInstance().postProject(project);
			if (stopSync == null || !stopSync) {
				syncPostServers(project);
			}
			return Response.status(Status.CREATED).entity(project.toString()).build();
		}
		return Response.status(Status.BAD_REQUEST).entity(
				new ConfResponse<Project>("-1000", "Project JSON is not valid, check Sample", POST_PROJECT_SAMPLE)
						.toString())
				.build();
	}

	@DELETE
	@Path("/{project}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteProperty(@PathParam("project") String project, @QueryParam("StopSync") Boolean stopSync) {
		Project proj = JPAHandler.getInstance().deleteProject(project);
		if (proj != null) {
			if (stopSync == null || !stopSync) {
				syncDeleteServers(project);
			}
			return Response.ok().entity(proj.toString()).build();
		}
		return Response.status(Status.NOT_FOUND)
				.entity(new ConfResponse<>("-1000", "KEY [" + project + "] not found").toString()).build();
	}

	private void syncPostServers(final Project project) {
		new Thread() {
			@Override
			public void run() {
				try {
					List<Server> servers = new ManageServers().getConfigServers();
					servers.stream().forEach(server -> {
						if (server.getPort() != request.getLocalPort() &&!Utils.ADDRESSES.contains(server.getIp())) {
							Client client = Client.create();
							WebResource resource = client.resource("http://" + server.getIp() + ":" + server.getPort()
									+ "/JConfServer/rest/Project?StopSync=true");
							resource.type(MediaType.APPLICATION_JSON);
							ClientResponse response = resource.post(ClientResponse.class, project);
							System.out.println(server.getPort() + "=>" + response.getStatus());
						}
					});

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}.start();
	}

	private void syncDeleteServers(final String project) {
		new Thread() {
			@Override
			public void run() {
				try {
					List<Server> servers = new ManageServers().getConfigServers();
					servers.stream().forEach(server -> {
						if (server.getPort() != request.getLocalPort() && !Utils.ADDRESSES.contains(server.getIp())) {
							Client client = Client.create();
							WebResource resource = client.resource("http://" + server.getIp() + ":" + server.getPort()
									+ "/JConfServer/rest/Project/" + project + "?StopSync=true");
							resource.type(MediaType.APPLICATION_JSON);
							ClientResponse response = resource.delete(ClientResponse.class);
							System.out.println(server.getPort() + "=>" + response.getStatus());
						}
					});

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}.start();
	}

}
