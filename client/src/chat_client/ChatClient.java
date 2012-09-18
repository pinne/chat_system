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

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

/** 
 * This class creates the GUI and handle outgoing packets (on the event 
 * dispatch thread).
 * 
 * Incoming packets are handled on a separate thread in the class 
 * Receiver.
 */
public class ChatClient extends JFrame {
	private static final long serialVersionUID = -7591669216369282173L;

	public static final int SERVER_PORT = 3490;
	
	private InetAddress iaddr;
	private int port;
	private Socket so = null;
	private DataOutputStream sout = null;
	private Receiver receiver;
	private JTextArea text = new JTextArea();
	private JTextField input = new JTextField(57); 
	private JButton button = new JButton("send");
	
	/**
	 * Constructor. Creates the socket, the receiver object and the GUI.
	 */
	public ChatClient(String server, int port) throws IOException {
		iaddr = InetAddress.getByName(server);
		this.port = port;
		this.so = new Socket(iaddr, port);

		sout = new DataOutputStream(so.getOutputStream());

		// Listener for window closing
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				send("/quit"); // Message to server...
				cleanUpAndExit();
			}	
		});
		// Called when user types ENTER in the text field.
		input.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send(input.getText());
				if (input.getText().equalsIgnoreCase("/quit"))
					cleanUpAndExit();
				input.setText("");
			}	
		});	
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send(input.getText());
				if (input.getText().equalsIgnoreCase("/quit"))
					cleanUpAndExit();
				input.setText("");
			}
		});

		// Layout stuff...
		setTitle("Chat client");
		text.setEditable(false);
		getContentPane().setLayout(new BorderLayout());
		JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		Font f = new Font("monospaced", Font.PLAIN, 20);
		text.setFont(f);
		
		input.requestFocus();
		input.setFont(f);
		input.setAlignmentY((float) 0.001);
		inputPanel.add(input);
		inputPanel.add(button);

		getContentPane().add(new JScrollPane(text), BorderLayout.CENTER);
		getContentPane().add(inputPanel, BorderLayout.PAGE_END);
		
		setVisible(true);
		setSize(800, 400);
		
		// Create a receiver object with a separate thread.
		receiver = new Receiver(so, text);
		receiver.start();
	}

	/**
	 * Send a message to the server.
	 */
	public void send(String msg) {
		try {
			sout.writeBytes(msg + "\n");
			sout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Closes the sockets and destroys the window before exit.
	 */
	private void cleanUpAndExit() {
		try {
			receiver.stop();
			so.close();
			dispose(); // Dispose window
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	/**
	 * Start an instance of ChatClient.
	 */
	public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			System.out.println("usage: java ChatClient servername");
			System.exit(0);
		}		
		
		// Look-n-feel of the system.
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
		// If exceptions occur during initialization, clean up and exit.
		ChatClient client = null;
		try {
			client = new ChatClient(args[0], SERVER_PORT);
		} catch (Exception e) {
			e.printStackTrace();
			if (client != null) {
				client.cleanUpAndExit();
			}
		}
	}
}
