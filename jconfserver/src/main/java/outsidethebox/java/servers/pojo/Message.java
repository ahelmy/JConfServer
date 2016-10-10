/**
 * 
 */
package outsidethebox.java.servers.pojo;

/**
 * @author AliHelmy
 *
 */
public class Message {
	private String server;
	private String method;
	private String object;
	private String data;

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
