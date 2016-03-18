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
    
    PrintWriter output;
    BufferedReader input;
    
    //Net related
    Socket socket;
    String username = "";
    List availableFiles;
     
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
        p.add(new JList(resultOfAvailableFiles), BorderLayout.SOUTH);
        
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
            
            
            while(true) {
            	String inputString = input.readLine();
            	chatBox.append(inputString+"\n");
            	
            }
            
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
                String userInput = inputField.getText();
                output.println(userInput);
                chatBox.append("Looking for " + userInput + "\n");
                System.out.println("Looking for " + userInput);
                inputField.setText("");
                //Search query results append here
                try {
                	input.wait(10000);
                    String result = input.readLine();
                    chatBox.append("Found " + result + "\n");
                    System.out.println("Found " + result);
                    
                    String[] resultOfAvailableFiles = {result};
                    p.add(new JList(resultOfAvailableFiles), BorderLayout.SOUTH);
                }
                catch(Exception exception)
                {
                    chatBox.append("Couldn't find " + userInput + "\n");
                    System.out.println("Couldn't find " + userInput);
                }

                
                
            } catch (Exception ex) {
                System.out.println(ex);
            }
    	}
    }
}