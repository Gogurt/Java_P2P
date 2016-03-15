package ChatExample;

import java.net.*;
import java.util.*;
import java.util.List;
import java.io.*;
import javax.swing.*;
import java.awt.*;
 
/* P2P Peer Directory Server
 * 3/13/15
 * A modified chat client repurposed to host a pool of
 * file directories from connecting peers. When a peer wishes
 * to download a file, they click on one of the available files
 * in the search query, which the server then contacts the owner
 * of the file to set up a direct connection with the requesting peer.
 */
public class Server extends JFrame
{
	//UI Related variables
    //A JTextArea to hold the information received from clients
    JTextArea chatBox = new JTextArea();
    
    //Network related variables
    int numberOfUsers = 0;
    private ArrayList<ClientThread> clientList;
    /*
     * Dedicated to paying attention to global list of available files.
     * A different instance of connected peers was created because threads
     * make it difficult to organize objects. That's why the Peer constructor was
     * also created.
     */
    private ArrayList<Peer> peerList;
    
    public static void main(String[] args)
    {
        new Server();
    }
    
    public Server()
    {
    	//UI Related
        //We need to set up a layout for our window
        setLayout(new BorderLayout());
        //Only display text, do not allow editing
        chatBox.setEditable(false);
        //Add our chatbox in the center with scrolling abilities
        add(new JScrollPane(chatBox), BorderLayout.CENTER);
        setTitle("Chat Server");
        setSize(550,400);
        //If the user closes then exit out
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Show the frame
        setVisible(true);
        
        //Network Related
        clientList = new ArrayList<ClientThread>();
        
        try 
        {
            ServerSocket sSocket = new ServerSocket(5000);
            chatBox.append("Server started at: " + new Date() + "\n");
            while(true)
            {
                Socket socket = sSocket.accept();
                
                //New thread is created per new peer connection
                ClientThread cT = new ClientThread(socket);
                //Adds the connected client to the list of know connected clients
                clientList.add(cT);
                numberOfUsers++;
                //Start the thread!
                new Thread(cT).start();
                 
                //Debug to check list of all connected clients
                /*
                chatBox.append("Connected users:\n");
                for(int i = 0; i < clientList.size(); i++)
                {
                	chatBox.append(clientList.get(i).threadSocket.getRemoteSocketAddress().toString() + "\n");
                }
                */
            }
        }
        catch(IOException exception)
        {
            System.out.println("Error: " + exception);
        }
    }
     
    //Connected Peer Thread
    class ClientThread implements Runnable
    {
        Socket threadSocket;
        String username;
        
        //May be used over printStream because of
        //multiple data types
        ObjectOutputStream objectOutput;
        
        ArrayList<String> availableFiles;
         
        //Defined thread constructor
        public ClientThread(Socket socket)
        {
            threadSocket = socket;
        }
         
        public void run()
        {
        	
            //All this should look familiar
            try {
                //Create the streams
                PrintWriter output = new PrintWriter(threadSocket.getOutputStream(), true);
                BufferedReader input = new BufferedReader(new InputStreamReader(threadSocket.getInputStream()));
                
                output.println("Welcome to the chat! There are " + numberOfUsers + " users online.");
                //output.println("Connected at " + new Date() + " Welcome to the chat! There are " + numberOfUsers + " users online.");
               
                
                //May allow other types besides strings over PrintWriter. haven't tested it yet.
                objectOutput = new ObjectOutputStream(threadSocket.getOutputStream());
 
                
                String chatInput = input.readLine();
                //Add the chat to the text box
                chatBox.append(chatInput+"\n");
                System.out.println(chatInput);
            	
                chatBox.append("Adding their file directories to available downloads...\n");
                System.out.println("Adding their file directories to available downloads...\n");
                
                //Appending all files sent from client until empty string is sent
                int amountOfFiles = 0;
                amountOfFiles = Integer.parseInt(input.readLine());
                
            	for(int i = 0; i < amountOfFiles; i++) {
            		String readInput = input.readLine();
                		chatBox.append("New file dir added: " + readInput + "\n");
                		System.out.println(readInput);
                		//availableFiles.add(readInput.toString());
            	}
            	
            	//Add new peer to list of available peers with files
            	//if(!availableFiles.isEmpty()) {
            		//Peer newPeer = new Peer(threadSocket.getRemoteSocketAddress().toString(), threadSocket.getPort(), availableFiles);
        			//peerList.add(newPeer);
            	//}
        		
            	System.out.println(availableFiles.toString());
            	System.out.println("File sync finished. Ready to recieve query data");
            	chatBox.append("File sync finished. Ready to recieve query data\n");
            	output.println("File sync finished. Ready to recieve query data");
            	
            	//while(true) {
            		//String queryInput = input.readLine();
            		
            		
            	//}
                
                    
                    
                    //Send to all connected peers
                    /*
                    for(int i = 0; i < clientList.size(); i++)
                    {
                    	output = new PrintWriter(clientList.get(i).threadSocket.getOutputStream(), true);
                    	output.println(chatInput);
                    }
                    */
                    //sendToAll(chatInput);
                    
                
            } catch(IOException exception) {
                System.out.println("Error: " + exception);
                //Remove this connected socket
                clientList.remove(this);
                numberOfUsers--;
            }
        }
        
        public void write(Object obj) {
            try{
            	objectOutput.writeObject(obj);
            }
            catch(IOException e){ e.printStackTrace(); }
        }
        
        //Supposed to send the Object message to all client sockets in clientList
        public void sendToAll(Object message){
            for(ClientThread client : clientList)
                client.write(message);
        }
        
    }
    
    public class Peer {
    	
    	private String ip;
		private int port;
		private ArrayList<String> availableFiles;

		public Peer(String ip, int port, ArrayList<String> availableFiles) {
    		this.ip = ip;
    		this.port = port;
    		this.availableFiles = availableFiles;
    	}
    }
}

