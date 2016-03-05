package Peer;
import java.awt.Color;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 * Peer to Peer System
 * 3/2/16
 * A simple peer to peer application that allows
 * connection to one specific centralized server,
 * and then further communication with other connected peers.
 */

public class Peer {
	
	static Socket peerSocket;
	static boolean connecting = false; //Added as debug between working on ui or the socket connection

	public static void main(String[] args) {
		//Net related
		if(connecting) {
			try {
				Socket clientSocket = new Socket("localhost", 555);
				System.out.println("Peer successfully created socket to " + clientSocket.getRemoteSocketAddress().toString());
				
				BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			    String answer = input.readLine();
			    System.out.println(answer);
			    
			    /*
			     * At this point, a persistent socket connection is instantiated,
			     * meaning certain options should be available on the ui to
			     * interact with the server through the currently connected socket.
			     */
				
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		//locally-defined variables
		 InetAddress ip;
		 JLabel ipLabel;
		  try {
			ip = InetAddress.getLocalHost();
			System.out.println("Current IP address : " + ip.getHostAddress());

			ipLabel = new JLabel("ip: " + ip.getHostAddress().toString());
		  } catch (UnknownHostException e) {
			e.printStackTrace();
			ipLabel = new JLabel("ip: Unknown!", SwingConstants.CENTER);
		  }
		
		//Menu Bar Creation
		JMenuBar menubar = new JMenuBar();
		JMenu file = new JMenu("File");
		menubar.add(file);
		JMenuItem upload = new JMenuItem("Upload");
		file.add(upload);
		
		//IpDisplay Label
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));// places at the left
		panel.setForeground(Color.white);
		panel.setOpaque(false);
		ipLabel.setForeground(Color.white);
		panel.add(ipLabel);
		
		//User Login Textbox
		JTextField jTextField = new JTextField();
        jTextField.setText("username");
        jTextField.setOpaque(false);
        jTextField.setForeground(Color.white);
        Border border = BorderFactory.createLineBorder(Color.WHITE, 1);
        jTextField.setBorder(border);
        panel.add(jTextField);
		
		//Frame creation
		JFrame frame = new JFrame("Peer 2 Peer");
		frame.setSize(640, 360);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setBackground(new Color(38,50,72));
		
		//Init of window contents before display
		frame.setJMenuBar(menubar);
		frame.add(panel);
		frame.setVisible(true);
		
	}

}