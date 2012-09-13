package chat_server;

import java.io.*;
import java.util.*;
import java.net.Socket;

/**
 * The class that handles a client in a separate thread
 */
class Session implements Runnable {
	
	private boolean connected = false;
	private Socket sock = null;
	private PrintWriter sout = null;
	private Broadcaster broadcaster;
	private static List<Session> sessions;
	
	Session(Socket sock, Broadcaster broadcaster, List<Session> sessions) {
		this.sock = sock;
		this.broadcaster = broadcaster;
		this.sessions = sessions;
		this.connected = true;
	}
	
	public Socket getSocket() {
		return sock;
	}
	
	// The thread activity, send a single message and then exit.
	public void run() {
		
		System.out.println("Running thread" + sock.getPort());
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

			// Get a stream for sending messages to client 
			// true = auto flush
			sout = new PrintWriter(sock.getOutputStream(), true);
			// Send a message
			sout.println("Welcome to the chat server <" + sock.getPort() + ">");				
			sout.println(printHelp());
			
			// Announce the new client
			broadcaster.sendStatus(this, "joined the server");
			
			while (connected) {
				String message = in.readLine();
				if (message.startsWith("/"))
					command(sout, message);
				else
					broadcaster.sendMessage(this, message);
			}
		}
		catch(IOException ie) {
			System.err.println(ie.toString());
		}
		finally {
			broadcaster.sendStatus(this, "left the server");
			sessions.remove(this);
			try {
				if(sock != null)
					sock.close();
			} catch(Exception e) {}
		}
	}

	/**
	 * Parse and execute client command.
	 */
	private void command(PrintWriter sout, String message) {
		if (message.equals("/quit")) {
			connected = false;
		} else if (message.equals("/who")) {
			sout.println(broadcaster.getWho());
		} else if (message.equals("/help")) {
			sout.println(printHelp());
		}
	}

	private String printHelp() {
		String str = new String();
		str = "Commands:\n"
				+ "/quit\t disconnects\n"
				+ "/who\t user list\n"
				+ "/help\t this information\n";
		return str;
	}
}
