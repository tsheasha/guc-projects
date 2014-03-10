import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

  public class GUI extends JFrame implements ActionListener {
	JLabel incoming;
	JButton send;
	JTextField input;
	Client client;

	public GUI (Client client) {
		try {
		this.client = client;

		this.setVisible(true);
		this.setLayout(null);
		this.setSize(600, 500);
		
		send = new JButton("Send");
		send.setSize(100,50);
        send.setLocation(480,5);
        send.addActionListener(this);
        
        input = new JTextField();
        input.setSize(450,50);
        input.setLocation(10,400);
        
        incoming = new JLabel();
        incoming.setLayout(null);
        incoming.setSize(450,50);
		incoming.setLocation(0,20);
		
		this.add(incoming);
		this.add(input);
		this.add(send);					
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void printFromServer(String serverSentence) {
    	try {
    
    	incoming.setText(serverSentence);
    	incoming.validate();
        incoming.repaint();
    	this.validate();
    	this.repaint();
    
    	} catch (Exception e) {
			e.printStackTrace();
		}    
    }
    
	public void actionPerformed(ActionEvent e) {
	try{
	
	if(e.getSource()==send)
	{
		
		String userSentence= input.getText();
		if(incoming.getText().contains("Please")) {
			client.setName(userSentence);
			this.setTitle(client.getName());
		}
		if (userSentence.equalsIgnoreCase("bye") || userSentence.equalsIgnoreCase("quit")) {
		client.respond(userSentence);
		client.commSocket.close();
		this.dispose();	 
		System.exit(0);
		}	
		else
		client.respond(userSentence);
		
			}
	} catch(Exception ex) {
	    ex.printStackTrace();
      }     
    }
    
    public static void main (String [] args) {
    /*	GUI w = new GUI();
    	w.incoming.setText("Adeek");
    	w.incoming.validate();
    	w.incoming.repaint();
    	w.validate();w.repaint();
    */}
 }