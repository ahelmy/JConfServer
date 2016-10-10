/**
 * 
 */
package outsidethebox.java.rest.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
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
import outsidethebox.java.rest.model.ConfResponse;
import outsidethebox.java.servers.ManageServers;
import outsidethebox.java.servers.pojo.Server;
import outsidethebox.java.utils.Utils;

/**
 * @author AliHelmy
 *
 */
@Path("/DBConnection")
public class DBConnectionService {

	private static DBConnection POST_DBCONNECTION_SAMPLE = new  DBConnection();
	static{
		POST_DBCONNECTION_SAMPLE.setConnectionID("some_ID");
		POST_DBCONNECTION_SAMPLE.setUsername("some_user");
		POST_DBCONNECTION_SAMPLE.setPassword("some_password");
		POST_DBCONNECTION_SAMPLE.setUrl("some_URL");
		POST_DBCONNECTION_SAMPLE.setDriver("some_driver");
	}
	
	@Context
	HttpServletRequest request;
	
	@GET
	@Path("/{connID")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
	public Response getDBConnection(@PathParam("connID") String connID) {
		DBConnection dbConnection = JPAHandler.getInstance().getDBConnection(connID);
		if (dbConnection != null) {
			return Response.ok().entity(dbConnection.toString()).build();
		}
		return Response.status(Status.NOT_FOUND).entity(new ConfResponse<>("KEY [" + connID + "] not found").toString())
				.build();
	}
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postPackage(DBConnection dbConnection, @QueryParam("StopSync") Boolean stopSync) {
		if (dbConnection != null && dbConnection.validPOST()) {
			JPAHandler.getInstance().postDBConnection(dbConnection);
			if (stopSync == null || !stopSync) {
				syncPostServers(dbConnection);
			}
			return Response.status(Status.CREATED).entity(dbConnection.toString()).build();
		}
		return Response.status(Status.BAD_REQUEST).entity(
				new ConfResponse<DBConnection>("-1000", "Project JSON is not valid, check Sample", POST_DBCONNECTION_SAMPLE)
						.toString())
				.build();
	}
	
	private void syncPostServers(final DBConnection dbConnection) {
		new Thread() {
			@Override
			public void run() {
				try {
					List<Server> servers = new ManageServers().getConfigServers();
					servers.stream().forEach(server -> {
						if (server.getPort() != request.getLocalPort() &&!Utils.ADDRESSES.contains(server.getIp())) {
							Client client = Client.create();
							WebResource resource = client.resource("http://" + server.getIp() + ":" + server.getPort()
									+ "/JConfServer/rest/DBConnection?StopSync=true");
							resource.type(MediaType.APPLICATION_JSON);
							ClientResponse response = resource.post(ClientResponse.class, dbConnection);
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
