package ChatExample;

import java.io.*;
import java.net.*;

//We no longer need a scanner to get input!
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
 
public class Client extends JFrame
{
	//UI Related
    JTextField inputField = new JTextField();
    JTextArea chatBox = new JTextArea();
    JPanel p;
    JList<String> resultsList;
    
    PrintWriter output;
    BufferedReader input;
    
    //Net Related
    Socket socket;
    String username = "";
    List availableFiles;
    
    //Foreign Peer Related
    Socket foreignPeerSocket;
    
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
        
    	String choice = JOptionPane.showInputDialog("Username: ");
        if(choice.length() != 0)
        {
        	username = choice;
        }
        else
        {
        	username = "anonymous user";
        }
        
        //Locate the users file folder and create a list of strings detailing those files
        System.out.println("Checking personal file folder directory...");
        File folder = new File("files");
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
            
            
        } catch (IOException exception) {
            System.out.println("Error: " + exception);
            chatBox.append("Failed to connect to server.");
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
                                     * If the result is FOUND_PEER, carry on with downloading process
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
		                                    
		                                    chatBox.append("Your requested file is from " + foreignUsername + " at " + foreignIP + "\n");
		                                    System.out.println("Your requested file is from " + foreignUsername + " at " + foreignIP);
		                                    //Establish a connection with the foreign peer
		                                    
		                                    
		                                    
		                                    
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
   

}