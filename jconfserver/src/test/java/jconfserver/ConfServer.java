/**
 * 
 */
package jconfserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

/**
 * @author AliHelmy
 *
 */
public class ConfServer extends Thread {

	/**
	 * @param args
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		ServerSocket server = new ServerSocket(3000);
		while (true) {
			Socket clientSocket = server.accept();
			ConfServer conf = new  ConfServer(clientSocket);
			conf.start();
		}
	}

	private Socket clientSocket;

	public ConfServer(Socket client) {
		clientSocket = client;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		try {
			handleClient();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleClient() throws IOException {
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		out.println("Welcome");
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String line = null;
		while ((line = in.readLine()) != null) {
			System.out.println(line);
			if (line.equals("bye")) {
				break;
			} else if (line.startsWith("echo")) {
				out.println(line.replaceFirst("echo", ""));
			}

		}
	}

}
