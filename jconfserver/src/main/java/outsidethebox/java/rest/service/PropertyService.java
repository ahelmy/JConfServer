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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import outsidethebox.java.backend.JPAHandler;
import outsidethebox.java.hibernate.models.DBConnection;
import outsidethebox.java.hibernate.models.Project;
import outsidethebox.java.hibernate.models.Property;
import outsidethebox.java.rest.model.ConfResponse;
import outsidethebox.java.servers.ManageServers;
import outsidethebox.java.servers.pojo.Server;
import outsidethebox.java.utils.Utils;

/**
 * @author AliHelmy
 *
 */
@Path("/Property")
public class PropertyService {

	public static final Property POST_PROPERTY_SAMPLE = new Property();
	static {
		POST_PROPERTY_SAMPLE.setName("some_name");
		POST_PROPERTY_SAMPLE.setValue("some_value");
		POST_PROPERTY_SAMPLE.setProject(ProjectService.POST_PROJECT_SAMPLE);
	}

	@Context
	HttpServletRequest request;

	@GET
	@Path("/{project}/{property}")
	@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
	public Response getPropery(@PathParam("project") String project, @PathParam("property") String property,
			@QueryParam("details") Boolean details) {
		Property prop;
		prop = JPAHandler.getInstance().getProperty(project, property);
		if (prop != null) {
			if (details != null && details) {
				return Response.ok().entity(prop.toString()).build();
			} else {
				return Response.ok().entity(prop.getValue()).build();
			}
		}

		return Response.status(Status.NOT_FOUND)
				.entity(new ConfResponse<>("KEY [" + project + "." + property + "] not found").toString()).build();
	}

	@GET
	@Path("/sample.json")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPropertyJSON() {
		Property prop = new Property();
		prop.setId(0);
		prop.setDescription("Property Description");
		prop.setName("Property1");
		prop.setUpdateEveryMin(0);
		prop.setValue("SELECT val FROM table");
		Project proj = new Project();
		proj.setId(0);
		proj.setName("Project1");
		proj.setDescription("Project1 Description");
		prop.setProject(proj);
		DBConnection db = new DBConnection();
		db.setId(0);
		db.setConnectionID("Unique connection ID");
		db.setDescription("DBConnection Description");
		db.setUrl("JDBC URL");
		db.setPassword("DB Password");
		db.setUsername("DB Uername");
		db.setDriver("JDBC Driver");
		prop.setDbconnection(db);
		prop.setProject(proj);
		return Response.ok().entity(prop.toString()).build();
	}

	/**
	 * 
	 * @param property
	 * @param stopSync
	 * @return
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postProperty(Property property, @QueryParam("StopSync") Boolean stopSync) {
		if (property != null && property.validPOST()) {
			JPAHandler.getInstance().postProperty(property);
			if (stopSync == null || !stopSync) {
				syncPostServers(property);
			}
			return Response.status(Status.CREATED).entity(property.toString()).build();
		}
		return Response.status(Status.BAD_REQUEST).entity(
				new ConfResponse<Property>("-1000", "Property JSON is not valid, check Sample", POST_PROPERTY_SAMPLE)
						.toString())
				.build();
	}

	@DELETE
	@Path("/{project}/{property}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteProperty(@PathParam("project") String project, @PathParam("property") String property,
			@QueryParam("StopSync") Boolean stopSync) {
		Property prop = JPAHandler.getInstance().deleteProperty(project, property);
		if (prop != null) {
			if (stopSync == null || !stopSync) {
				syncDeleteServers(project, property);
			}
			return Response.ok().entity(prop.toString()).build();
		}
		return Response.status(Status.NOT_FOUND)
				.entity(new ConfResponse<>("-1000", "KEY [" + project + "." + property + "] not found").toString())
				.build();
	}

	private void syncPostServers(final Property property) {
		new Thread() {
			@Override
			public void run() {
				try {
					List<Server> servers = new ManageServers().getConfigServers();
					servers.stream().forEach(server -> {
						if (server.getPort() != request.getLocalPort() && !Utils.ADDRESSES.contains(server.getIp())) {
							Client client = Client.create();
							WebResource resource = client.resource("http://" + server.getIp() + ":" + server.getPort()
									+ "/JConfServer/rest/Property?StopSync=true");
							resource.type(MediaType.APPLICATION_JSON);
							ClientResponse response = resource.post(ClientResponse.class, property);
							System.out.println(server.getPort() + "=>" + response.getStatus());
						}
					});

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}.start();
	}

	private void syncDeleteServers(String project, String property) {
		new Thread() {
			@Override
			public void run() {
				try {
					List<Server> servers = new ManageServers().getConfigServers();
					servers.stream().forEach(server -> {
						if (server.getPort() != request.getLocalPort() && !Utils.ADDRESSES.contains(server.getIp())) {
							Client client = Client.create();
							WebResource resource = client.resource("http://" + server.getIp() + ":" + server.getPort()
									+ "/JConfServer/rest/Property/" + project + "/" + property + "?StopSync=true");
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
