package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Java P2P Server Application
 * 3/4/16
 * A server that listens, connects, and multithreads
 * multiple peers. For each peer connected, the server sends out
 * a current status of how many peers are connected to the running
 * application. At this point, peers can also make additional server requests
 * to interact with other connected peers.
 */
public class Server {

	public static int serverPort = 555;
	
	public static void main(String[] args) {
		System.out.println("Starting P2P Directory Server...");
		
		try {
			ServerSocket serverSocket = new ServerSocket(serverPort);
			System.out.println("Successfully created server socket " + serverSocket.getInetAddress().toString() + " on port " + serverPort);
			while(true) {
			    	System.out.println("Listening for connecting peers...");
					Socket peerSocket = serverSocket.accept();
			        
	                System.out.println("Heard response from " + peerSocket.getRemoteSocketAddress().toString());
			        Thread peerThread = new PeerSocketThread(peerSocket);
			        peerThread.start();
			        
			        /*
			         * For some reason, 'Listening for connecting peers' doesn't show up after the
			         * thread has been started. This indicates that the loop possibly isn't looping.
			         */
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

class PeerSocketThread extends Thread {
	public PeerSocketThread(Socket peerSocket) throws Exception {
		
		boolean connected = true;
		
		System.out.println("Threaded a new connected peer: " + peerSocket.getRemoteSocketAddress().toString());
		InetAddress inet = peerSocket.getInetAddress();
        PrintWriter out = new PrintWriter(peerSocket.getOutputStream(), true);
        out.println("Server has accepted your connection! Starting persistant socket connection...");
        
        while(connected)
        {
        	BufferedReader input = new BufferedReader(new InputStreamReader(peerSocket.getInputStream()));
		    String answer = input.readLine();
		    if(answer.length() != 0) {
		    	System.out.println("From peer " + answer);
		    }
		    //Checks to see if peer has disconnected
		    if(peerSocket.isClosed()) {
		    	connected = false;
		    }
        }
        
        System.out.println("Disconnecting peer socket...");
        peerSocket.close();
		
	}
}