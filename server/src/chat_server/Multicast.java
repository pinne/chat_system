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

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The Multicast class handles the collection of Sessions, every session
 * communicates to other sessions through this object.
 */
public class Multicast {
	private List<Session> sessions;

	public Multicast(ServerSocket sock) {
		sessions = new ArrayList<Session>();
	}

	/**
	 * Sends a chat message to the connected clients.
	 */
	public synchronized void sendMessage(Session from, String message) {
		PrintWriter writer;
		System.out.println("<" + from.getNick() + "> " + message);

		synchronized (sessions) {
			for (Session s : sessions) {
				try {
					writer = new PrintWriter(s.getSocket().getOutputStream(), true);
					writer.println("<" + from.getNick() + "> " + message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Sends a status message to the connected clients.
	 */
	public synchronized void sendStatus(Session from, String message) {
		PrintWriter writer;
		System.out.println("-!- " + from.getNick() + " " + message);

		synchronized (sessions) {
			for (Session s : sessions) {
				try {
					writer = new PrintWriter(s.getSocket().getOutputStream(), true);
					writer.println("-!- " + from.getNick() + " " + message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Return a formatted string of connected clients.
	 */
	public String getWho() {
		String who = new String();

		synchronized (sessions) {
			for (Session s : sessions) {
				who = "[" + s.getNick() + "]\n" + who;
			}
		}
		return who;
	}

	/**
	 * When a client disconnect, this removes a session from the
	 * collection upon a request for the terminating session.
	 */
	public void dropSession(Session session) {
		synchronized (sessions) {
			sessions.remove(session);
		}
	}
	
	public void addSession(Session s) {
		synchronized (sessions) {
			sessions.add(s);
		}
	}
}
