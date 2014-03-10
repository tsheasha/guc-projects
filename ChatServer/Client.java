
import java.io.*;
import java.lang.ClassNotFoundException; 
import java.lang.Runnable; 
import java.lang.Thread; 
import java.net.*;


public class Client  {
	Socket commSocket;
	private String name;
	DataOutputStream out;
    BufferedReader in;
    BufferedReader stdin;
    GUI window;
  	public Client(int port) {
		try {		

		commSocket = new Socket (/*"192.168.1.4"*/"localhost", port);
		

		in = new BufferedReader
		(new InputStreamReader(commSocket.getInputStream()));
		
		out =new DataOutputStream(commSocket.getOutputStream());
		
		stdin = new BufferedReader
		(new InputStreamReader(System.in));
		
		name = "";
		window = new GUI(this);
        execute();
		
		} catch(Exception e) {
			e.printStackTrace();
		}
		}
	
	public void execute() {
	String line = "";
	String input = "";
		try {

			line = in.readLine();
		    window.printFromServer(line);
			
			String name2 = stdin.readLine();
			this.name = name2;
			out.writeBytes(name + "\n");
		     
		    line = in.readLine();
			window.printFromServer(line);
			
			
		} catch (Exception e) {
				e.printStackTrace();
		}
		}
	
	
	public void listen() {

    new  Thread() {

    public synchronized void run()	{  
        try {
		
		while(true) {
		
	String line = in.readLine();
	window.printFromServer(line);
        }
      } catch (SocketException e) {
   		 	e.printStackTrace();
   		 	 } catch (Exception e) {
   		 	 e.printStackTrace();
   		 	 }
		}
			
   }.start();	
 }	
 
    public void respond (String input) {
    try {
    		if (input != null){
    		String source = this.name;
			String TTL = "3";
			String destination = "";
		
			if (input.equalsIgnoreCase("list")) {
			destination = this.name;
			source = this.name;
			}
			
			for(int i = 0;  input.contains(":") && i < input.length(); i++) { 

		 if(input.charAt(i) == ':') {
		 	input = input.substring(i + 1);
		 	break; 
		 } else { 
		 	destination += input.charAt(i); 
		 }
	 }

		 String message = input;
	     String response  = source+"%"+destination+"%"+TTL+"%"+message;

    	out.writeBytes(response + "\n");
    	} else{
    	out.writeBytes(input + "\n");
    	}
		this.listen();

   		 } catch (SocketException e) {
   		 		e.printStackTrace();

   		 	 } catch (Exception e) {
   		 	 		e.printStackTrace();
   		 	 }
   		 }
     
	public static void main (String [] args) throws Exception {
//	Client c = new Client(6000);

	}
	
	
	public void setName(String s) {
		this.name = s;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setSocket(Socket s) {
		this.commSocket = s;
	}
	
	public Socket getSocket() {
		return this.commSocket;
	}
}
