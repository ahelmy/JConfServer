/**
 * 
 */
package outsidethebox.java.servers.pojo;

import java.util.Date;

import com.google.gson.GsonBuilder;

/**
 * @author AliHelmy
 *
 */
public class Server {
	private String name;
	private int sequence;
	private String ip;
	private int port;
	private long startUp;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public long getStartUp() {
		return startUp;
	}

	public void setStartUp(long startUp) {
		this.startUp = startUp;
	}

	@Override
	public String toString() {
		return new GsonBuilder().setPrettyPrinting().create().toJson(this);
	}

}
