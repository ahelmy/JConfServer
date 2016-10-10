/**
 * 
 */
package jconfserver;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import outsidethebox.java.servers.ManageServers;
import outsidethebox.java.servers.pojo.Message;
import outsidethebox.java.servers.pojo.Server;

/**
 * @author AliHelmy
 *
 */
public class TestServers {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		List<Server> servers = new  ManageServers().getConfigServers();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		servers.stream().forEach(s -> System.out.println(gson.toJson(s)));
		
	}

}
