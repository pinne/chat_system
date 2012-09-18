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
import java.net.Socket;

/**
 * The class that handles a client in a separate thread.
 */
class Session implements Runnable {
	
	private PrintWriter stream = null;
	private Multicast multicaster;
	private String nick = "";
	
	private Socket sock = null;
	public Socket getSocket() {
		return sock;
	}
	
	private boolean connected = false;
	
	/**
	 * Every session holds a reference to the common multicast object.
	 */
	public Session(Socket sock, Multicast multicaster) {
		this.sock = sock;
		this.multicaster = multicaster;
		this.connected = true;
		
		// Get a random nick. A feature, not a bug!
		Nicks nicks = new Nicks();
		this.nick = nicks.getNick();
	}
	
	/**
	 * Session thread, receives messages from the connected client.
	 */
	public void run() {
		System.out.println("Running thread " + nick + ": " + sock.toString());
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));

			// Get a stream for sending messages to client 
			stream = new PrintWriter(sock.getOutputStream(), true);
			// Send a message
			stream.println("Welcome to the chat server <" + nick + ">");				
			stream.println(printHelp());
			
			// Announce the new client
			multicaster.sendStatus(this, "joined the server");
			
			while (connected) {
				String message = in.readLine();
				if (message == null)
					break;
				if (message.startsWith("/"))
					command(stream, message);
				else
					multicaster.sendMessage(this, message);
			}
		} catch(IOException ie) {
			System.err.println(ie.toString());
		} finally {
			// Close the connection and remove the session from the list.
			multicaster.sendStatus(this, "left the server");
			multicaster.dropSession(this);
			
			if (stream != null)
				stream.close();
				
			try {
				if(sock != null) {
					sock.close();
				}
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
			sout.println(multicaster.getWho());
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

	public String getNick() {
		return nick;
	}
}
