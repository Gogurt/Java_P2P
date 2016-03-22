package ChatExample;

import java.io.*;
import java.net.*;

//We no longer need a scanner to get input!
import javax.swing.*;

import ChatExample.Server.ClientThread;

import java.awt.*;
import java.awt.event.*;
 
public class Client extends JFrame
{
	//UI Related
    JTextField inputField = new JTextField();
    JTextArea chatBox = new JTextArea();
    JPanel p;
    JPanel b;
    JTextField directoryField;
    JList<String> resultsList;
    JFileChooser directoryChooser;
    JButton fileButton;
    String directory;
    
    PrintWriter output;
    BufferedReader input;
    
    //Net Related
    Socket socket;
    String username = "";
    List availableFiles;
    
    //Foreign Peer Related
    Socket foreignSocket;
    //Specifically defined and sent to main server so multiple peers can be on one computer.
    int foreignPortListener = 0;
    
    public final static int FILE_SIZE = 6022386;

    public static void main(String[] args)
    {
        new Client();
    }
     
    public Client()
    {
        //Create a panel with the UI for getting input from user
        p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(new JLabel("Search:"), BorderLayout.WEST);
        p.add(inputField, BorderLayout.CENTER);
        
        //The panel for creating a username and setting the directory path
        b = new JPanel(new GridLayout(3, 2, 0, 0));
        
        b.add(new JLabel("Username: "));
        JTextField unField = new JTextField(10);
        b.add(unField);
 
        directoryField = new JTextField(15);
        
        //fileButton uses JFileChooser to open a file window and allow selection of a folder
        fileButton = new JButton("Choose");
        fileButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent ae) {
        		directoryChooser = new JFileChooser();
        		directoryChooser.setDialogTitle("Choose Directory");
        		directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        		directoryChooser.setAcceptAllFileFilterUsed(false);
        		
        		if(directoryChooser.showOpenDialog(directoryChooser) == JFileChooser.APPROVE_OPTION) {
        			System.out.println("getCurrentDirectory(): " 
        			         + directoryChooser.getSelectedFile());
        			directoryField.setText(directoryChooser.getSelectedFile().toString());
        		}
        		else {
        			System.out.println("No directory selected");
        		}
        	}
        });
        b.add(new JLabel("Directory: "));
        b.add(new JLabel());
        b.add(directoryField);
        b.add(fileButton);
        b.setSize(400, 300);
        b.setPreferredSize(new Dimension(400, b.getPreferredSize().height));
        
        //This is where search query results are appended
        String[] resultOfAvailableFiles = {"Query search outputs here..."};
        resultsList = new JList<String>(resultOfAvailableFiles);
        resultsList.setEnabled(false);
        p.add(resultsList, BorderLayout.SOUTH);
        
        setLayout(new BorderLayout());
        //Add the chatBox and the panel for getting user input
        chatBox.setEditable(false);
        add(new JScrollPane(chatBox), BorderLayout.CENTER);
        add(p, BorderLayout.SOUTH);
        
         
        //Add an action listener on the text field 
        //so we can get what the user is typing
        inputField.addActionListener(new TextFieldListener());
        
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
         
        //Housekeeping stuff
        setTitle("P2P Application");
        setSize(550,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
        System.out.println(directory);
        
        //JOptionPane for users to enter their username and directory or exit
    	int result = JOptionPane.showConfirmDialog(null, b, "Enter Username and Directory", JOptionPane.OK_CANCEL_OPTION);
    	
    	if(result == JOptionPane.OK_OPTION) {
    		if(unField.getText().length() != 0)
    		{
    			username = unField.getText();
    		}
    		else
    		{
    			username = "anonymous user";
    		}
    		
    		directory = directoryField.getText();
    		
    		
    		
    	}
    	else if(result == JOptionPane.CANCEL_OPTION) {
    		this.dispose();
    		System.exit(0);
    	}
    	
    	/*
    	 * This is where the foreign listening port is being defined before it is sent
    	 * with the other information to the directory server
    	 */
    	ServerSocket sSocket = null;
        System.out.println("Defining available port for foreign peer listener...");
		chatBox.append("Defining available port for foreign peer listener...\n");
		//Checking availability of ports
		int n = 5001;
		while(foreignPortListener == 0)
		{
			if(available(n))
			{
				foreignPortListener = n;
				System.out.println("Defined listening port on " + foreignPortListener);
		        chatBox.append("Defined listening port on " + foreignPortListener + "\n");
			}
			else
			{
				n++;
			}
		}
    	
    	
        
        //Locate the users file folder and create a list of strings detailing those files      
        System.out.println("Checking personal file folder directory...");
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        availableFiles = new List();
        
            for (int i = 0; i < listOfFiles.length; i++) {
              if (listOfFiles[i].isFile()) {
                System.out.println(listOfFiles[i].getName());
                availableFiles.add(listOfFiles[i].getName().toString());
              } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
                System.out.println("Alert: Only files will be implemented right now, folders will not be included");
              }
            }
            
            System.out.println(availableFiles.getItemCount() + " file[s] will be synced...");
            
        try {
            socket = new Socket("localhost", 5000);
            output = new PrintWriter(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            output.println(username);
            output.println(foreignPortListener);
            
            //Sending number indicating how many files first
            output.println(availableFiles.getItemCount());
            for(int i = 0; i < availableFiles.getItemCount(); i++) {
            	output.println(availableFiles.getItem(i));
            }
            output.flush();
            
            String inputMeta = input.readLine();
        	chatBox.append(inputMeta+"\n");
        	String inputWelcome = input.readLine();
        	chatBox.append(inputWelcome+"\n");
        	String inputFileSyncStatus = input.readLine();
        	chatBox.append(inputFileSyncStatus+"\n");
            
            
        } catch (IOException exception) {
            System.out.println("Error: " + exception);
            chatBox.append("Failed to connect to server.");
        }
        
        /*
         * It's only after the Peer has connected and synced its file
         * contents does it open up for another peer to download from them.
         * There should be some level of authentication to prevent peers from
         * constantly downloading from you without permission, but for testing
         * purposes, this should be just fine.
         * Similar to the Server class, a socket is used to listen for connecting peers,
         * which are then thrown into their own Thread class for searching the request and
         * returning the file to the requesting peer.
         * For testing on one machine, both peers need to have their own specific port,
         * A method has been added earlier to allow the choice of a port, which was also sent to the server.
         * So one peer should be 5001 and another 5002.
         */
        try {
        	//Socket created with previously defined port we know isn't in use
			sSocket = new ServerSocket(foreignPortListener);
            while(true)
            {
            	System.out.println("Listening for foreign peer requests");
                chatBox.append("Listening for foreign peer requests\n");
    			Socket foreignSocket = sSocket.accept();
    			
    			int portNum = foreignSocket.getPort();
    			System.out.println(portNum);

                if(foreignPortListener != 5001)
                {
        			System.out.println("Foreign peer detected. Creating new thread to handle download...");
                    chatBox.append("Foreign peer detected. Creating new thread to handle download...\n");
        			//Pass connection to a new thread to transfer the requested file
                    //The thread method still needs to be made, so nothing will happen right now.
                    System.out.println("Connecting foreign peer on port " + foreignSocket.getPort());
                    ForeignPeerThread cT = new ForeignPeerThread(foreignSocket, directory, availableFiles);
    				new Thread(cT).start();
                }
                else
                {
                	foreignSocket.close();
                }
                
            }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
    }
    
    //This class handles the text field
    class TextFieldListener implements ActionListener
    {
        //This method will be called when the user hits enter
        public void actionPerformed(ActionEvent e) {
            try {
                output.println("Request_Search");
                String userInput = inputField.getText();
                output.println(userInput);
                chatBox.append("Looking for " + userInput + "\n");
                System.out.println("Looking for " + userInput);
                inputField.setText("");

                //Search query results append here
                    String result = input.readLine();
                    if(result.equals("nothing"))
                    {
                        chatBox.append("Couldn't find " + userInput + "\n");
                        System.out.println("Couldn't find " + userInput);
                        String[] resultOfAvailableFiles = {"Query search outputs here..."};
                        p.remove(resultsList);
                        resultsList = new JList<String>(resultOfAvailableFiles);
                        resultsList.setEnabled(false);
                        p.add(resultsList, BorderLayout.SOUTH);
                        p.revalidate();
                        p.repaint();
                    }
                    else
                    {
                        chatBox.append("Found " + result + "\n");
                        System.out.println("Found " + result);
                        
                        String[] resultOfAvailableFiles = {result};
                        p.remove(resultsList);
                        resultsList = new JList<String>(resultOfAvailableFiles);
                        p.add(resultsList, BorderLayout.SOUTH);
                        p.revalidate();
                        p.repaint();
                        

                        resultsList.addMouseListener(new MouseAdapter() {
                            public void mouseClicked(MouseEvent evt) {
                                JList<String> list = (JList)evt.getSource();
                                if (evt.getClickCount() == 2) {

                                    // Double-click detected
                                    int index = list.locationToIndex(evt.getPoint());
                                    String selectedItem = resultsList.getSelectedValue().toString();
                                    System.out.println("Double click detected on element " + selectedItem);
                                    
                                    //Sending download request query
                                    chatBox.append("Attempting to download " + selectedItem + "\n");
                                    System.out.println("Attempting to download " + selectedItem);
                                    //Primes the server to let it know the next output is a download query
                                    output.println("Request_Download");
                                    output.println(selectedItem);
                                    
                                    /*
                                     * If the result is PEER_FOUND, carry on with downloading process
                                     */
                                    try {
										String result = input.readLine().toString();

	                                    if(result.equals("PEER_FOUND"))
										{
											chatBox.append("Found peer with requested file! beginning connection process...\n");
		                                    System.out.println("Found peer with requested file! beginning connection process...");
		                                    //Grab ip information about other peer
		                                    String foreignIP = input.readLine().toString();
		                                    String foreignUsername = input.readLine().toString();
		                                    int foreignPort = Integer.parseInt(input.readLine());
		                                    
		                                    chatBox.append("Your requested file is from " + foreignUsername + " at " + foreignIP + "\n");
		                                    System.out.println("Your requested file is from " + foreignUsername + " at " + foreignIP);
		                                    //Establish a connection with the foreign peer
		                                    //5001 for now will be an open socket listening for connecting ips.
		                                    //Once connected, a thread is used to search and send back the requested file
		                                    try {
		                                    	//IP may be a problem. 'localhost' should also work just fine if this doesn't work
		                                    	foreignSocket = new Socket("localhost", foreignPort);
 		                              
		                                    	PrintWriter foreignOutput = new PrintWriter(foreignSocket.getOutputStream(), true);
		                                        BufferedReader foreignInput = new BufferedReader(new InputStreamReader(foreignSocket.getInputStream()));
		                                    	//Begin transfer process once the other peers listening port passes us to our own thread
		                                        foreignOutput.println(username + " has connected to your client to download " + selectedItem);
		                                        foreignOutput.println(selectedItem);
		                                        
		                                        //Define place to store file
		                                        String fileOutputDir = directory + "/" + selectedItem;
		                                    	
		                                    	//Recieve file from foreign peer
		                                        int bytesRead;
		                                        int current = 0;
		                                        FileOutputStream fos = null;
		                                        BufferedOutputStream bos = null;
		                                        
		                                        byte [] mybytearray  = new byte [FILE_SIZE];
		                                        InputStream is = foreignSocket.getInputStream();
		                                        fos = new FileOutputStream(fileOutputDir);
		                                        bos = new BufferedOutputStream(fos);
		                                        
		                                        bytesRead = is.read(mybytearray,0,mybytearray.length);
		                                        
		                                        current = bytesRead;

		                                        do {
		                                        	System.out.println("Reading " + current + " bytes...");
		                                        	bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
		                                        	if(bytesRead >= 0) current += bytesRead;

		                                        } while(current < FILE_SIZE);

	                                        	bos.write(mybytearray, 0 , current);
		                                        bos.flush();
		                                        System.out.println("File " + fileOutputDir + " downloaded (" + current + " bytes read)");
		                                    	
		                                        if (fos != null) fos.close();
		                                        if (bos != null) bos.close();
		                                        if (foreignSocket != null) foreignSocket.close();
		                                        
		                                        
		                                    }
		                                    catch(IOException ioe)
		                                    {
		                                    	chatBox.append("Error: Unable to communicate with " + foreignUsername + "\n");
			                                    System.out.println("Error: Unable to communicate with " + foreignUsername);
		                                    }
										}
										else
										{
											chatBox.append(result.toString() + "\n");
		                                    System.out.println("Error: Could not find peer with requested file!");
										}
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
                                    
                                }
                            }
                        });
                        
                    }
                    inputField.setText("");

            } catch (Exception ex) {
                System.out.println(ex);
            }
    	}
    }
 
    public static boolean available(int port) {
        if (port < 5000 || port > 6000) {
            throw new IllegalArgumentException("Invalid start port: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }

}

/*
 * Handles incoming peers asking for a particular download
 */
class ForeignPeerThread implements Runnable
{
	Socket foreignPeerSocket;
	String directory;
	List availableFiles;
	
	ObjectOutputStream objectOutput;
	PrintWriter output;
	BufferedReader input;
	
	FileInputStream fis = null;
    BufferedInputStream bis = null;
    OutputStream os = null;
	
	public ForeignPeerThread(Socket foreignPeerSocket, String directory, List availableFiles)
    {
		this.foreignPeerSocket = foreignPeerSocket;
		this.directory = directory;
		this.availableFiles = availableFiles;
    }
	
	public void run() {
		System.out.println("Thread running");
		try {
			output = new PrintWriter(foreignPeerSocket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(foreignPeerSocket.getInputStream()));
			
			System.out.println(input.readLine().toString());
			String requestedItem = input.readLine().toString();
			//Checking for requested item. If found, begin sending process
			for(int i = 0; i < availableFiles.getItemCount(); i++)
			{
				if(availableFiles.getItem(i).equals(requestedItem))
				{
					try
					{
						String fileToSend = directory + "\\" + requestedItem;
						FileInputStream readIn = new FileInputStream(fileToSend);
						
						File myFile = new File (fileToSend);
				        byte [] mybytearray  = new byte [(int)myFile.length()];
				        fis = new FileInputStream(myFile);
				        bis = new BufferedInputStream(fis);
				        bis.read(mybytearray,0,mybytearray.length);
				        os = foreignPeerSocket.getOutputStream();
				        System.out.println("Sending " + fileToSend + "(" + mybytearray.length + " bytes)");
				        os.write(mybytearray,0,mybytearray.length);
				        os.flush();
				        System.out.println("Done.");
				        
				   

					}
					catch(IOException e)
					{
						System.out.println(e);
					}
				
				}
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}