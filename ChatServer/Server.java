
import java.io.*;
import java.net.*;
import java.util.*;

	class Communication implements Runnable {
		private Socket dataSocket;
		private BufferedReader stdin;
		private DataOutputStream outToClient;
		DataOutputStream outToClient2;
		private BufferedReader inFromServer;
		private DataOutputStream outToServer;
		
		String memberName; 
		LinkList clients;
		String source = "";
		String toSearch = "";
		
		Socket next;
		Socket arriving;
		
		String fromServer = "";
		String incoming = "";

		public Communication (Socket s, LinkList clients, Socket n, Socket arr) {
		
		try {	
		
		next = n;
		arriving = arr;	
		dataSocket = s;
		
		memberName = "";
		this.clients = clients;
        
        stdin = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));	
	    outToClient =new DataOutputStream(dataSocket.getOutputStream());
	    
	    inFromServer = new BufferedReader(new InputStreamReader(arriving.getInputStream()));	
	    outToServer =new DataOutputStream(next.getOutputStream());

	} catch (Exception e) {
		
		e.printStackTrace();	
		
			}
		}

		public void run() {
			try {			
		outToClient.writeBytes("Please Enter Your Name To Join This Server" + "\n");
		boolean firstRun = true;
	
		while(true) {
			String []k = new String [4];
			String msg = " ";
		    fromServer = "";
		
			incoming = "";

	        if(firstRun) {
			incoming = stdin.readLine();
			k = incoming.split("%");
		    if (k.length == 4)
		    msg= k[3];
		    else
		    msg = "";
		    }

	
if(!firstRun) {

	new  Thread() {
    public synchronized void run()	{  
        try {
        	boolean viewedList = false;
while (true) {
   incoming = stdin.readLine();
   //System.out.println(incoming + " incoming");
String [] sub = incoming.split("%");
      if (incoming.length() > 0 && (sub[3].equalsIgnoreCase("bye") ||
	sub[3].equalsIgnoreCase("quit"))) {
	closing();
	return;
			} else	
	if(incoming.length() > 0){
	if(sub[3].equalsIgnoreCase("list")) {
	viewedList = true;
	outToServer.writeBytes(incoming + "\n");
	} else
	if (viewedList)
    search_and_send(incoming);
    else
    outToClient.writeBytes("You Must FIRST view the List of Members" + "\n");
	}	
			

incoming = "";
			}
			} catch (Exception e) {
   		 	 		e.printStackTrace();
   		 	 }
   		 	 }
		
	}.start();
  
   new  Thread() {

    public synchronized void run()	{  

        try {

while(true) {

		fromServer = inFromServer.readLine();
    
	String [] sub = fromServer.split("%");
	if(fromServer.length() > 0) {
		
		if (sub[3].startsWith("list")) {
		 	search_and_send(fromServer + clients.returnList());
		} else
		search_and_send(fromServer); 	
		}		
 

	//System.out.println(fromServer + " fromserver");
	fromServer = "";
		
}
} catch (Exception e) {
	e.printStackTrace();
	 	 }
		}
	}.start();
   
 
 
  
 
 
break;	
}
		
		if (firstRun) {
			boolean exists = false;
			Link current = clients.first;
	           if(current != null)		
				while(current.next != null) {
					if(current.data.memberName.equalsIgnoreCase(msg)) {
					exists = true;
					break;
					} else {
					current = current.next;
					}
				}
				
				if(current != null && current.data.memberName.equalsIgnoreCase(msg)) {
				 exists = true;
				 }
			
			if(exists) {
             outToClient.writeBytes(
"This Name is Already being Used, Please Enter Another Name" + "\n");

			} else 
		if(msg.length() == 0) {
             outToClient.writeBytes(
"You did not Enter a Name, Please Enter Your Name" + "\n");
			} else {	
			this.memberName = msg;	
		    clients.insertLast(this);
			outToClient.writeBytes(
"Welcome " + this.memberName + " To Send/Receive Messages, Type 'List' to view Members" + "\n"); 
			firstRun = false;
			}	
          }
		}

			} catch (SocketException e) {
   		 		e.printStackTrace();   		 	
   		 	 } catch (Exception e) {
	e.printStackTrace();
   		 	 }
		}

			public void writeToClient(Link client, String s, String from) {
		
		try {
		outToClient2 =new DataOutputStream(client.data.dataSocket.getOutputStream());
		if(s.startsWith("list"))
		outToClient2.writeBytes("Member List : " + s.substring(4) + "\n");
		else
		outToClient2.writeBytes(from + " writes : " + s + "\n");
		
		} catch (Exception e) {
		e.printStackTrace();
		}
	}
	
	public String search_and_send(String incoming) {
	
	try {   
			         
			boolean found = false;
			String destination = "";
		    boolean destSpecified = false;
			String message = "";
            String from = "";
            String [] header = new String [4];
			  header = incoming.split("%");
		    int	TTL = Integer.parseInt(header[2]);
			if(header[0].startsWith("srv")) {
				incoming = incoming.substring(3);
				header = incoming.split("%");
			}
		
		Link current = clients.first;
			if (header[1] != "" && header[1] !=null) {
			destination = header[1];
			destSpecified = true;
			message = header[3];
			from = header[0];
			}
			
			if(!destSpecified) {
				outToClient.writeBytes(
"Kindly Specify a Desination by writing the Receiver followed by ':' first" + "\n");
return"";
			} else {
			
        while(current != null && !found) {
		    if(current.data.memberName.equals(destination)) {
		       found = true;
		        break;
		        }   

			    if(current.next != null){
				current = current.next;
				} else {
					if (TTL == 0) {
					return"";
					} else {
						TTL--;
						header[2] = ""+TTL;
					incoming = "srv"+header[0]+"%"+header[1]+"%"+header[2]+"%"+header[3];
				outToServer.writeBytes(incoming + "\n");
				return"";
					}
				}
		}
	
		
		if(found) {
         writeToClient(current, message, from);			   
	    return"";		
		} 		
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}	
		
return"";	
} 
	
	public void closing() throws Exception {
	clients.deleteLink(this.memberName);
    stdin.close();
	dataSocket.close();
	return;
	}
		
	}
	public class Server {

	private ServerSocket connectionSocket;
	private ServerSocket ServerConnectionSocket1;
	private ServerSocket ServerConnectionSocket2;

    Socket next; 
    Socket arriving;
	Socket serverConnection;
	Socket serverConnection2;


	LinkList clients;
	int clientPort; 
	int serverPort1; 
	int serverPort2; 
	int connection1; 
	int connection2;

	
	public Server(int cP, int sP1, int sP2, int c1, int c2) {
clientPort  = cP;
serverPort1 = sP1;
serverPort2 = sP2;
connection1 = c1; 
connection2 = c2;
		try {

	
connectionSocket  = new ServerSocket(clientPort);	

     
		
	if(serverPort1 != 0) {
		new  Thread() {
    public synchronized void run()	{  
        try {

        	ServerConnectionSocket1 = new ServerSocket(serverPort1);
		    next = ServerConnectionSocket1.accept();
			

      } catch (SocketException e) {
      		e.printStackTrace();

   		 	 } catch (Exception e) {
   		 	 		e.printStackTrace();
   		 	 }
		}
			
   }.start();
   }
				
			if(serverPort2 != 0) {
		new  Thread() {
    public synchronized void run()	{  
        try {

        	ServerConnectionSocket2 = new ServerSocket(serverPort2);
		arriving = ServerConnectionSocket2.accept();
			

      } catch (SocketException e) {
      		e.printStackTrace();

   		 	 } catch (Exception e) {
   		 	 		e.printStackTrace();
   		 	 }
		}
			
   }.start();	
			}
			if(connection1 != 0) {
			new  Thread() {
    public synchronized void run()	{  
        try {

        	serverConnection = new Socket("localhost", connection1);
			

      } catch (SocketException e) {
      		e.printStackTrace();

   		 	 } catch (Exception e) {
   		 	 		e.printStackTrace();
   		 	 }
		}
			
   }.start();
   }	
    
   if(connection2 != 0) {
			new  Thread() {
    public synchronized void run()	{  
        try {

        	serverConnection2 = new Socket("localhost", connection2);			

      } catch (SocketException e) {
      		e.printStackTrace();

   		 	 } catch (Exception e) {
   		 	 		e.printStackTrace();
   		 	 }
		}
			
   }.start();	
   }
				
		 clients = new LinkList();

		 execute();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void execute () {
try {

		while (true) {
			if(serverPort1 > 0 && serverPort2 > 0) {								
	Thread t = new Thread (new Communication(connectionSocket.accept(), clients, next, arriving));
			t.start();	
			} else
				if(serverPort1 > 0 && serverPort2 == 0) {								
	Thread t = new Thread (new Communication(connectionSocket.accept(), clients, next, serverConnection));
			t.start();	
			} else
				if(serverPort1 == 0 && serverPort2 == 0) {								
	Thread t = new Thread (new Communication(connectionSocket.accept(), clients, serverConnection, serverConnection2));
			t.start();	
			}
		}
			
			
			} catch (Exception e) {			
			e.printStackTrace();	
			}			
	}
	
	public static void main (String [] args) {
	//	Server s  = new Server(6000, 6500, 8500, 0, 0);
	//	Server s  = new Server(7000, 7500, 0, 6500, 0);
	//	Server s  = new Server(8000, 0, 0, 8500, 7500);
		}
	
}
