package ChatExample;

import java.io.*;
import java.net.*;

//We no longer need a scanner to get input!
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
 
public class Client extends JFrame
{
    JTextField inputField = new JTextField();
    JTextArea chatBox = new JTextArea();
    
    PrintWriter output;
    BufferedReader input;
    
    String username = "";
    
    //Net related
    Socket socket;
    
    List availableFiles;
     
    public static void main(String[] args)
    {
        new Client();
    }
     
    public Client()
    {
        //Create a panel with the UI for getting input from user
        JPanel p = new JPanel();
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
         
        //Housekeeping stuff
        setTitle("P2P Application");
        setSize(550,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
        username = "anonymous user";
        
    	String choice = JOptionPane.showInputDialog("Username: ");
        if(choice.length() != 0)
        {
        	username = choice;
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
            
            
            output.println(username + " has connected!");
            for(int i = 0; i < availableFiles.getItemCount(); i++) {
            	output.println(availableFiles.getItem(i));
            }
            output.println("FILESEND");
            
            //This will wait for the server to send the string to the client saying a connection
            //has been made.
            String inputString = input.readLine();
            chatBox.append(inputString+"\n");
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
                output.println(username + ": " + userInput);
                chatBox.append("You: " + userInput+"\n");
                inputField.setText("");
            } catch (Exception ex) {
                System.out.println(ex);
            }
    	}
    }
}