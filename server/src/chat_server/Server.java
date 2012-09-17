/**
 * Chat system - Server
 * 
 * Communication Systems, HI1032
 * Lab assignment 3 - Client-Server programming
 * 
 * Simon Kers      skers@kth.se
 * Sakib Pathan    sakibp@kth.se
 *                                 KTH STH 2012
 */

package chat_server;

import java.net.*;
import java.util.*;
import java.io.*;

/**
 * Listen for incoming client connections, start a Session thread per
 * connection with a reference to the common Multicast object.
 */
public class Server {

	public static final int PORT = 3490;
	private static List<Session> sessions = new ArrayList<Session>();

	public static void main(String[] args) throws IOException {
		ServerSocket servSock = null;

		try {
			// Create a server socket, and bind it to a local port
			servSock = new ServerSocket(PORT);

			// Create multicaster for sending messages to everyone
			Multicast multicaster = new Multicast(sessions, servSock);

			// Listen to incoming connections.
			while(true) {
				System.out.println("Server is listening...");
				Socket sock = servSock.accept();
				System.out.println("Server contacted from " + 
						sock.getInetAddress());

				Session client = new Session(sock, multicaster);
				new Thread(client).start();

				synchronized (sessions) {
					sessions.add(client);
				}
			}
		}
		// Close the  server socket...
		finally {
			System.out.println("Closing server socket");
			try {
				if(servSock != null)
					servSock.close();
			} catch(Exception e) {
			}
		}
	}
}
