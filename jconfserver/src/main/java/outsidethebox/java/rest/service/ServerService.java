/**
 * 
 */
package outsidethebox.java.rest.service;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

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

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.jersey.api.client.ClientResponse.Status;

import outsidethebox.java.hibernate.models.Property;
import outsidethebox.java.rest.model.ConfResponse;
import outsidethebox.java.servers.ManageServers;
import outsidethebox.java.servers.pojo.Server;
import outsidethebox.java.servlet.ConfigurationServletListener;
import outsidethebox.java.utils.Utils;

/**
 * @author AliHelmy
 *
 */
@Path("/Server")
public class ServerService {

	@Context
	HttpServletRequest request;

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCurrentServer() {
		Server server = null;
		try {
			int localPort = request.getLocalPort();
			server = new ManageServers().getConfigServers().stream()
					.filter(s -> Utils.ADDRESSES.contains(s.getIp()) && s.getPort() == localPort).findAny()
					.orElse(new Server());
			server.setStartUp(ConfigurationServletListener.START_UP);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return Response.ok().entity(server.toString()).build();
	}

	@GET
	@Path("/Sync")
	@Produces(MediaType.APPLICATION_JSON)
	public Response syncServer(@QueryParam("ServerName") String serverName) {
		try {
			Utils.syncServer(serverName);
			JsonObject obj = new  JsonObject();
			obj.addProperty("status", "Synchronized");
			return Response.ok().entity(new GsonBuilder().setPrettyPrinting().create().toJson(obj)).build();
		} catch (Exception ex) {
			return Response.status(Status.NOT_FOUND)
					.entity(new ConfResponse<Property>("-1000", ex.getMessage()).toString())
					.build();
		}

	}
}
