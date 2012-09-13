package chat_server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Broadcaster {
	private static List<Session> sessions;

	public Broadcaster(List<Session> sessions, ServerSocket sock) {
		Broadcaster.sessions = sessions;
	}
	
	public synchronized void sendMessage(Session client, String message) {
		PrintWriter writer;
		System.out.println("<" + client.getSocket().getPort()+ "> " + message);

		for (Session s : sessions) {
			try {
				writer = new PrintWriter(s.getSocket().getOutputStream(), true);
				writer.println("<" + client.getSocket().getPort()+ "> " + message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized void sendStatus(Session client, String message) {
		PrintWriter writer;
		System.out.println("-!- " + client.getSocket().getPort()+ " " + message);

		for (Session s : sessions) {
			try {
				writer = new PrintWriter(s.getSocket().getOutputStream(), true);
				writer.println("-!- " + client.getSocket().getPort()+ " " + message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getWho() {
		String who = new String();
		
		for (Session s : sessions) {
			who = "[" + s.getSocket().getPort() + "]\n" + who;
		}
		return who;
	}

}
