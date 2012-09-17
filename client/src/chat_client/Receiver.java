/**
 * Chat system - Client
 * 
 * Communication Systems, HI1032
 * Lab assignment 3 - Client-Server programming
 * 
 * Simon Kers      skers@kth.se
 * Sakib Pathan    sakibp@kth.se
 *                                 KTH STH 2012
 */

package chat_client;

import java.io.*;
import java.net.*;
import javax.swing.*;

/** 
 * The class Receiver handle incoming packets in a separate thread.
 */
class Receiver implements Runnable {

	private Thread activity = null;
	private Socket socket;
	private JTextArea textArea;

	Receiver(Socket so2, JTextArea textArea) {
		this.socket = so2;
		this.textArea = textArea;
	}

	public void run() {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			while (Thread.currentThread() == activity) {
				String msg = in.readLine();
				System.out.println(msg);
				appendText(msg); // Add msg to textArea in a thread safe manner!
			}
		} catch (IOException ie) {
			appendText("An exception ocurred: " + ie.toString());
		}
		finally {
			try {
				in.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			appendText("Stopped receiving");
		}
	}

	void start() {
		if(activity == null) {
			activity = new Thread(this);
			activity.start();
		}
	}

	void stop() {
		activity = null;
	}

	/**
	 * Append text to the text area, from outside the
	 * event dispatch thread, in a thread safe manner.
	 */
	void appendText(final String msg) {
		SwingUtilities.invokeLater(  // Add to the event dispatch threads queue
				new Runnable() {
					public void run() {
						textArea.append(msg + "\r\n");
					}
				}
		);
	}
}
