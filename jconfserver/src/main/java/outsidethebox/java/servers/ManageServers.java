/**
 * 
 */
package outsidethebox.java.servers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import outsidethebox.java.servers.pojo.Server;
import outsidethebox.java.utils.ResourceUtils;

/**
 * @author AliHelmy
 *
 */
public class ManageServers {

	@Context
	HttpServletRequest request;

	public List<Server> getConfigServers() throws IOException {
		String fileName = "config/servers.json";
		
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new FileReader(ResourceUtils.getFile(fileName).getAbsolutePath().replaceAll("%20", " ")));
		return Arrays.asList(gson.fromJson(reader, Server[].class));
	}

	public void syncServers() throws IOException {
		List<Server> servers = getConfigServers();
		servers.stream().forEach(server -> {
			try {
				if (!server.getIp().equals(Inet4Address.getLocalHost().getHostAddress())) {
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});
	}
}
