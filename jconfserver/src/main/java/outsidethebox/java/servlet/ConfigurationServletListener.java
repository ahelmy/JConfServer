/**
 * 
 */
package outsidethebox.java.servlet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import outsidethebox.java.backend.JPAHandler;
import outsidethebox.java.hibernate.JPA;
import outsidethebox.java.hibernate.models.Project;
import outsidethebox.java.hibernate.models.Property;
import outsidethebox.java.servers.ManageServers;
import outsidethebox.java.servers.pojo.Server;
import outsidethebox.java.utils.Utils;

/**
 * @author AliHelmy
 *
 */
public class ConfigurationServletListener implements ServletContextListener {

	public static long START_UP = 0;

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.ServletContextListener#contextInitialized(javax.servlet.
	 * ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		START_UP = System.currentTimeMillis();
		Utils.listIPs();
		try {
			Utils.syncServer(null);
			JPAHandler.getInstance().loadProperties();
			JPAHandler.getInstance().loadDBConnections();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
	 * ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub

	}

}
