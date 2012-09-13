package chat_server;

/** A Java stream socket server demo. The application uses the 
 *  Socket and ServerSocket classes.
 *  The application listens on a ServerSocket object. When a new 
 *  connection is made by a client, the server socket spawns a new
 *  socket and a separate thread to handle that client is created
 *  (class ClientHandler).
 *  The server socket thread (main) continues to listen for new
 *  connections.
 *  In this version, the client thread sends and exits.
 *  Server port: 3490.
 *  Usage: java ThreadedTCPSever
 */

import java.net.*;
import java.util.*;
import java.io.*;

public class Server {
	
	public static final int PORT = 3490;
	private static List<Session> sessions = new ArrayList<Session>();
	
	public static void main(String[] args) throws IOException {
		ServerSocket servSock = null;

		try {
			// Create a server socket, and bind it to a local port
			servSock = new ServerSocket(PORT);
			
			// Create broadcaster for sending messages to everyone
			Broadcaster broadcaster = new Broadcaster(sessions, servSock);
			
			// Listen to incoming connections, create a separate
			// client handler thread for each connection
			while(true) {
				System.out.println("Server is listening...");
				Socket sock = servSock.accept();
				System.out.println("Server contacted from " + 
						sock.getInetAddress());
				// Create a new clienthandler
				Session client = new Session(sock, broadcaster, sessions);
				new Thread(client).start();
				sessions.add(client);
			}
		}
		// Close the  server socket...
		finally {
			System.out.println("Closing server socket");
			try {
				if(servSock != null)
					servSock.close();
			} catch(Exception e) {}
		}
	}
	
}
