package outsidethebox.java.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import outsidethebox.java.hibernate.JPA;
import outsidethebox.java.hibernate.models.DBConnection;
import outsidethebox.java.hibernate.models.Project;
import outsidethebox.java.hibernate.models.Property;
import outsidethebox.java.servers.ManageServers;
import outsidethebox.java.servers.pojo.Server;

public class Utils {

	public static final List<String> ADDRESSES = new LinkedList<>();

	public static void listIPs() {
		try {
			Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
			for (; n.hasMoreElements();) {
				NetworkInterface e = n.nextElement();

				Enumeration<InetAddress> a = e.getInetAddresses();
				for (; a.hasMoreElements();) {
					InetAddress addr = a.nextElement();
					if (addr.isSiteLocalAddress())
						ADDRESSES.add(addr.getHostAddress());
				}
			}
		} catch (SocketException ex) {

		}
	}

	public static void syncServer(String serverName) throws Exception {
		try {
			List<Server> confServers = new ManageServers().getConfigServers();
			List<Server> currentServers = new LinkedList<>();
			Server serverToSyncWith = null;

			if (serverName != null) {
				serverToSyncWith = confServers.stream().filter(s -> s.getName().equals(serverName)).findAny()
						.orElse(null);
				if (serverToSyncWith == null) {
					throw new Exception("Server name [" + serverName + "] not found");
				}
			} else {
				confServers.forEach(s -> {
					checkServer(s, currentServers);
				});
				if (!currentServers.isEmpty()) {
					// Sort and get first
					currentServers.sort((f, s) -> (int) (f.getStartUp() - s.getStartUp()));
					serverToSyncWith = currentServers.get(0);
				}
			}
			syncServer(serverToSyncWith);
			/*		*/
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void syncServer(Server server) {
		/* Start Sync Process */
		Client client = Client.create();
		WebResource resource = client
				.resource("http://" + server.getIp() + ":" + server.getPort() + "/JConfServer/rest/Project");
		ClientResponse response = resource.get(ClientResponse.class);
		if (response.getStatus() == 200) {
			Project[] projects = response.getEntity(Project[].class);
			final JPA jpa = new JPA();
			Arrays.asList(projects).forEach(p -> {
				Project tmp = jpa.getProject(p.getName());
				try {
					if (tmp != null) {
						p.setId(tmp.getId());
						jpa.saveProject(p);
					} else {
						jpa.saveProject(p, false);
					}
				} catch (Exception ex) {

				}
				p.getProperties().forEach(prop -> {
					try {
						Property tmpProp = jpa.getProperty(prop);
						prop.setProject(p);

						if (prop.getDbconnection() != null) {
							DBConnection dbConn = jpa.getDBConnection(prop.getDbconnection().getConnectionID());
							if (dbConn != null) {
								prop.getDbconnection().setId(dbConn.getId());
								jpa.saveDBConnection(prop.getDbconnection());
							} else {
								jpa.saveDBConnection(prop.getDbconnection(), false);
							}
						}

						if (tmpProp != null) {
							prop.setId(tmpProp.getId());
							jpa.saveProperty(prop);
						} else {
							jpa.saveProperty(prop, false);
						}
					} catch (Exception ex) {
ex.printStackTrace();
					}
				});

			});
		}
	}

	private static void checkServer(Server s, List<Server> servers) {
		try {
			Client client = Client.create();
			WebResource resource = client
					.resource("http://" + s.getIp() + ":" + s.getPort() + "/JConfServer/rest/Server");
			ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
			if (response.getStatus() == 200) {
				Server resServer = response.getEntity(Server.class);
				servers.add(resServer);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
