package ChatExample;

import java.net.*;
import java.util.*;
import java.util.List;
import java.io.*;
import javax.swing.*;
import java.awt.*;
 
public class Server extends JFrame
{
    //A JTextArea to hold the information received from clients
    JTextArea chatBox = new JTextArea();
    
    int numberOfUsers = 0;
    private ArrayList<ClientThread> clientList;
    
    
    
    public static void main(String[] args)
    {
        new Server();
    }
    
    public Server()
    {
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
        
        clientList = new ArrayList<ClientThread>();
        //We need a try-catch because lots of errors can be thrown
        try {
            ServerSocket sSocket = new ServerSocket(5000);
            chatBox.append("Server started at: " + new Date() + "\n");
            
            //Loop that runs server functions
            while(true) {
                //Wait for a client to connect
                Socket socket = sSocket.accept();
                
                
                
                //Create a new custom thread to handle the connection
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
        } catch(IOException exception) {
            System.out.println("Error: " + exception);
        }
    }
     
    //Here we create the ClientThread inner class and have it implement Runnable
    //This means that it can be used as a thread
    class ClientThread implements Runnable
    {
        Socket threadSocket;
        String username;
        
        ObjectOutputStream objectOutput;
        
        List availableFiles;
         
        //This constructor will be passed the socket
        public ClientThread(Socket socket)
        {
            //Here we set the socket to a local variable so we can use it later
            threadSocket = socket;
        }
         
        public void run()
        {
        	
            //All this should look familiar
            try {
                //Create the streams
                PrintWriter output = new PrintWriter(threadSocket.getOutputStream(), true);
                BufferedReader input = new BufferedReader(new InputStreamReader(threadSocket.getInputStream()));
                
                //Tell the client that he/she has connected
                //Can't send more than one out.println for some reason
                output.println("Welcome to the chat! There are " + numberOfUsers + " users online.");
                //output.println("Connected at " + new Date() + " Welcome to the chat! There are " + numberOfUsers + " users online.");
               
                
                //May allow other types besides strings over PrintWriter. haven't tested it yet.
                objectOutput = new ObjectOutputStream(threadSocket.getOutputStream());
 
                
        
                
                while (true) {
                    String chatInput = input.readLine();
                    //Add the chat to the text box
                    chatBox.append(chatInput+"\n");
                    System.out.println(chatInput);
                	
                    chatBox.append("Adding their file directories to available downloads...\n");
                    System.out.println("Adding their file directories to available downloads...\n");
                    
                    //Appending all files sent from client until FILEEND
                	while(true) {
                		String readInput = input.readLine();
                		if(readInput == "FILESEND") {
                			break;
                		} else {
                    		chatBox.append("New file dir added: " + readInput + "\n");
                    		System.out.println(readInput);
                		}
                		//Add file names to list here

                	}
                	System.out.println(availableFiles.toString());
                
                    
                    
                    //Send to all connected peers
                    /*
                    for(int i = 0; i < clientList.size(); i++)
                    {
                    	output = new PrintWriter(clientList.get(i).threadSocket.getOutputStream(), true);
                    	output.println(chatInput);
                    }
                    */
                    sendToAll(chatInput);
                    
                }
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
		private List availableFiles;

		public Peer(String ip, int port, List availableFiles) {
    		this.ip = ip;
    		this.port = port;
    		this.availableFiles = availableFiles;
    	}
    }
}

