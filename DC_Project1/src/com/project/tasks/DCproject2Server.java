package com.project.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;

import com.project.framework.Task;

public class DCproject2Server extends SimplePersistentTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7795512019459062027L;
	
	
	protected Socket clientSocket;
    public static ArrayList<serverFile> clientfiles;  //list of client files and the host who have them
    public static int numClients;
    public static int serverID;
    public static ArrayList<serverLoads> servers;
	
	@Override
	public void executeTask() {
		
		startServer();
    }

	@Override
	public Task fromBytes(byte[] byteArray) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onFinished() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProgressUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] toBytes() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void startServer() {
		Random rand = new Random();
        serverID = rand.nextInt(5000);
        numClients = 0;
        serverLoadBalancer SLB = new serverLoadBalancer();
        SLB.start();
        ServerSocket serverSocket = null;
        BCthread clientConnector = new BCthread(); //spawn the client broadcast authenticator thread
        serverFileQuery sFQ = new serverFileQuery(); //spawn the thread that responds to file queries
        sFQ.start();
        clientConnector.start();


        clientfiles = new ArrayList<serverFile>(); //start as a new list
        try
        {
            serverSocket = new ServerSocket(6666);         //create a socet for clients to connect to
            System.out.println ("data socket created");
            try
            {
                while (true)                              //forever
                {
                    System.out.println ("Waiting for a connection....");
                    	acceptClient(serverSocket.accept());      //if they connect to us spawn thread
                }
            }
            catch (IOException execp)
            {
                System.out.println("something went wrong accepting connection : "+execp);
            }
        }
        catch (IOException execp)
        {
            System.out.println("something went wrong creating port: " + execp);
        }
 }
	
//	private Main(Socket clientSoc)
//    {
//        clientSocket = clientSoc;  //with new connection start thread
//        start();
//    }

	public class serverFileQuery extends Thread {
		
		public serverFileQuery() {
			
		}
		
		@Override
		public void run() {
			try
	        {
	            DatagramSocket socket = new DatagramSocket(6666); //UDP socket to listen on,
	            String requestedFile;
	            InetAddress clientIP;
	            int clientPort;
	            while (true)   //forever
	            {
	                socket.setSoTimeout(10000);
	                System.out.println("Waiting for file request");
	                byte[] recBuffer = new byte[15000];
	                DatagramPacket packet = new DatagramPacket(recBuffer,recBuffer.length);
	                socket.receive(packet);   //read an incoming packet
	                clientIP = packet.getAddress();
	                clientPort = packet.getPort();
	                System.out.println("File request received from "+packet.getAddress().getHostAddress());
	                String message = new String(packet.getData()).trim();
	                requestedFile = message;
	                if(message.equals("###SERVER###"))
	                {
	                    socket.receive(packet);   //read an incoming packet
	                    System.out.println("File request received from a server");
	                    message = new String(packet.getData()).trim();
	                    for(int i = 0; i<DCproject2Server.clientfiles.size();++i)         //for all of our files on record
	                    {
	                        if(DCproject2Server.clientfiles.get(i).fName.equals(message)) //if the message send matches a file name
	                        {                                                 //respond with the first host in the list(ip and port)
	                            String temp = DCproject2Server.clientfiles.get(i).hosts.get(0).IPaddress+":"+DCproject2Server.clientfiles.get(i).hosts.get(0).port;
	                            byte[] sendData = temp.getBytes();
	                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,packet.getAddress(),packet.getPort());
	                            socket.send(sendPacket);
	                            System.out.println("Responded with "+DCproject2Server.clientfiles.get(i).hosts.get(0).IPaddress+":"+DCproject2Server.clientfiles.get(i).hosts.get(0).port);
	                            break;
	                        }
	                    }
	                }
	                else
	                {
	                    boolean found=false;
	                    for(int i = 0; i<DCproject2Server.clientfiles.size();++i)         //for all of our files on record
	                    {
	                        if(DCproject2Server.clientfiles.get(i).fName.equals(message)) //if the message send matches a file name
	                        {                                                 //respond with the first host in the list(ip and port)
	                            String temp = DCproject2Server.clientfiles.get(i).hosts.get(0).IPaddress+":"+DCproject2Server.clientfiles.get(i).hosts.get(0).port;
	                            byte[] sendData = temp.getBytes();
	                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,packet.getAddress(),packet.getPort());
	                            socket.send(sendPacket);
	                            System.out.println("Responded with "+DCproject2Server.clientfiles.get(i).hosts.get(0).IPaddress+":"+DCproject2Server.clientfiles.get(i).hosts.get(0).port);
	                            found = true;
	                            break;
	                        }
	                    }
	                    if(!found) //it not listed on this server
	                    {
	                        String temp = "###SERVER###";
	                        byte[] sendData = temp.getBytes();
	                        for(int i = 0; i<DCproject2Server.servers.size();++i)//ask all servers
	                        {
	                            if (DCproject2Server.servers.get(i).idNum!=DCproject2Server.serverID)
	                            {
	                                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(DCproject2Server.servers.get(i).serverIP),6666);
	                                socket.send(sendPacket);
	                                sendData = requestedFile.getBytes();
	                                sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(DCproject2Server.servers.get(i).serverIP),6666);
	                                socket.send(sendPacket);
	                            }
	                        }
	                        socket.receive(packet);   //read an incoming packet i hope its correct i'm not going to check it!
	                        message = new String(packet.getData()).trim();
	                        sendData = message.getBytes();
	                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIP,clientPort);
	                        socket.send(sendPacket);       //forward anything recieved back to the client... it had better be correct
	                                                        //if i have time i'll put the server sync stuff into another thread on a diff port


	                    }
	                }
	            }
	        }
	        catch(IOException except)
	        {
	            System.out.println("Something went wrong: "+except);
	        }
	    }
	}
	
	public void acceptClient(final Socket socket) {
		
		new Thread() {
			Socket clientSocket = socket;
			
			@Override
			public void run() {
				try
		        {
		            BufferedReader Istream = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));

		            node tempNode = new node(); //temporary new representation of a client node

		            tempNode.IPaddress = (clientSocket.getInetAddress().getHostAddress()); //get the ip address of this client

		            String temp = Istream.readLine(); //get the first message
		            if(temp.substring(0, 3).equals("###"))//if its the port number
		            {
		                tempNode.port = Integer.parseInt(temp.substring(3,temp.length())); //get the port number
		            }
		            else
		                tempNode.port = 8888; //some how we errored and didnt get the port first... so use the default

		            System.out.println("We have a client messaging from "+tempNode.IPaddress+":"+tempNode.port);

		            serverFile tempSF; //create a temporary new file representation

		            clientSocket.setSoTimeout(100000);

		            //to account for deleted files, remove all records of this client
		            for(int i = 0; i < clientfiles.size(); ++i)                     //for all file names
		            {
		                for (int j = 0; j < clientfiles.get(i).hosts.size(); ++j)  //for all clients that host it
		                {
		                    if(clientfiles.get(i).hosts.get(j).IPaddress.equals(tempNode.IPaddress)) //if this client is a host
		                    {
		                        clientfiles.get(i).hosts.remove(j); //remove them as a host
		                    }
		                }
		                if( clientfiles.get(i).hosts.isEmpty())//if no one hosts this file remove it
		                {
		                    clientfiles.remove(i);
		                }
		            }

		            while(true)   //while there are still files
		            {
		                tempSF = new serverFile();    //temp new server file
		                tempSF.hosts = new ArrayList<node>(); //with a new arraylist initialization
		                tempSF.fName = Istream.readLine();    //get the file name

		            //    System.out.println("The client sent : "+tempSF.fName);

		                if(tempSF.fName.equals("EOF"))         //if its the end of the list break the loop
		                {
		                    break;
		                }

		                tempSF.hosts.add(tempNode);           //this new file obviously came from our temp client node

		                boolean existing = false;             //if its an existing file name or not

		                for (int i =0;i<clientfiles.size();++i)   //for all file names
		                {
		                    if(clientfiles.get(i).fName.equals(tempSF.fName)) //if we have the file name already
		                    {
		                    //    System.out.println("We already have this file on record");
		                        existing = true;           //it already exists
		                        if(!clientfiles.get(i).hosts.contains(tempNode)) //if we dont have this client listed (we shouldnt)
		                        {
		                    //        System.out.println("but not on this host");
		                            clientfiles.get(i).hosts.add(tempNode);     //add this host to the list for that file
		                            break;
		                        }
		                        else          //else it already existed.... some how?
		                        {
		                    //      System.out.println("We already have this file and host on record");
		                          break;
		                        }
		                    }
		                }
		                if(!existing)        //if we didnt find it add it.
		                {
		            //        System.out.println("We had no record, adding it now");
		                    clientfiles.add(tempSF);
		                }

		            }
		            OutputStream Ostream = clientSocket.getOutputStream();
		            temp = "";
		            for(int i = 0; i<clientfiles.size(); ++i)       //for all files,
		            {
		                temp = clientfiles.get(i).fName+"\n";
		                System.out.println("Sending : "+temp);
		                Ostream.write(temp.getBytes()); //tell the client the list we have on record
		            }
		            for(int i=0;i<servers.size();++i) //get the list from another server
		            {
		                if(serverID!=servers.get(i).idNum)//if its not me
		                {
		                    Socket otherServers = new Socket(servers.get(i).serverIP,6666);
		                    BufferedReader inpstream = new BufferedReader( new InputStreamReader(otherServers.getInputStream()));
		                    OutputStream   oupstream =  otherServers.getOutputStream();
		                    temp = "EOF\n";
		                    oupstream.write(temp.getBytes()); //trigger the server to send us its list back
		                    while(true)
		                    {
		                        temp = inpstream.readLine(); //read each recieved line
		                        if (temp.equals("EOF")) //if its the end from the server break the while loop
		                            break;
		                        Ostream.write(temp.getBytes());//forward it on to the client!
		                    }
		                    Ostream.flush();     //close it!!
		                    Ostream.close();
		                    inpstream.close();
		                    otherServers.close();
		                }
		            }

		            System.out.println("Finished sending out list to the client");
		            temp = "EOF\n";                                               //send end of list escape sequence
		            Ostream.write(temp.getBytes());

		            Ostream.close();                                            //close everything
		            System.out.println("sent an EOF");
		            clientSocket.close();
		        }
		        catch (Exception e)
		        {
		            System.err.println("Problem with Server :"+e);

		        }
			}
		};
	}
	
	public class serverFile
	{
	    String fName;
	    ArrayList<node> hosts;
	}
	
	public class node
	{
	    String IPaddress;
	    int port;
	}
	
	public class serverLoadBalancer extends Thread
	{
	    DatagramSocket socket;
	    public void run()
	    {

	    	DCproject2Server.servers = new ArrayList<serverLoads>();

	        while(true)
	        {
	            try {
	                byte[] senddata = "@@@".getBytes();            //prepare valid client data for sending to server
	                DatagramSocket clientN = new DatagramSocket(); //create a new socket
	                clientN.setBroadcast(true);                    //enable broadcasting

	                try { //send to the highest order broadcast address
	                    DatagramPacket sendPacket = new DatagramPacket(senddata, senddata.length, InetAddress.getByName("255.255.255.255"), 6785);
	                    clientN.send(sendPacket);
	                }
	                catch (Exception excep) //this should never fail
	                {
	                    System.out.println("Failed to broadcast to : 255.255.255.255");
	                }
	                //next load all network interfaces into a list
	                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
	                while (interfaces.hasMoreElements()) //for all network interfaces
	                {
	                    NetworkInterface networkInterface = interfaces.nextElement();
	                    if (networkInterface.isLoopback() || !networkInterface.isUp())//if its a 127.0.0.1 (local address) or not connected
	                        continue;                                                 //skip it
	                    for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
	                        InetAddress broadcast = interfaceAddress.getBroadcast();
	                        if (broadcast == null) //if broadcast isnt allowed
	                            continue;          //skip it
	                        try {
	                            DatagramPacket sendPacket = new DatagramPacket(senddata, senddata.length, broadcast, 6785);
	                            clientN.send(sendPacket);   //send the packet to the broadcast on all valid interfaces

	                        } catch (Exception excep)
	                        {
	                            System.out.println("Error broadcast to : rest of broadcast pools");
	                        }
	                    }
	                }
	                byte[] recBuffer = new byte[15000];

	                DatagramPacket receivePacket = new DatagramPacket(recBuffer, recBuffer.length);
	                clientN.receive(receivePacket); //receive a response, hopefully its from the server

	                String temp = new String(receivePacket.getData()).trim(); //trim extra chars like \n
	                serverLoads tempSL = new serverLoads();
	                if(temp.substring(0, 3).equals("%%%"))//if its a server responding
	                {
	                   String[] parts = temp.substring(3,temp.length()).split(":");
	                   tempSL.idNum = Integer.parseInt(parts[0]);
	                   tempSL.numClients = Integer.parseInt(parts[1]);
	                   tempSL.serverIP = receivePacket.getAddress().getHostAddress();

	                   boolean found = false;
	                   for(int i =0; i< DCproject2Server.servers.size(); ++i)
	                   {
	                       if(DCproject2Server.servers.get(i).idNum==tempSL.idNum)
	                       {
	                    	   DCproject2Server.servers.get(i).numClients = tempSL.numClients;
	                           found = true;
	                           break;
	                       }
	                   }
	                   if(!found)
	                       DCproject2Server.servers.add(tempSL);

	                }
	            }
	            catch (Exception excep)
	            {
	                System.out.println("failed on something major : "+excep);
	            }
	        }
	    }



	}
	
	public class serverLoads
	{
	    int idNum;
	    int numClients;
	    String serverIP;
	}
	
	public class BCthread extends Thread
	{
	    DatagramSocket socket;  //listening socket
	    @Override
	    public void run()
	    {
	        try
	        {
	            socket = new DatagramSocket(6785); //listen on a specific port
	            socket.setBroadcast(true);         //we want to listen to broadcasts
	            String myaddy = "%%%";             //Originally was going to send an IP, but decided to use 3 chars just
	            while (true)                       // so the client could authenticate the type of message it recieved.
	            {   //forever
	               // System.out.println("Waiting for clients");//output to console if present the current status
	                byte[] recBuffer = new byte[15000];       //buffer for received information
	                DatagramPacket packet = new DatagramPacket(recBuffer,recBuffer.length); //packet for received information
	                socket.receive(packet); //receive the broadcast message
	               // System.out.println("Packet received from "+packet.getAddress().getHostAddress());// output that we found one

	                String message = new String(packet.getData()).trim();//trim any extra chars off the end \n etc

	                if(message.equals("$$$")) //if the message is a valid signal from a client
	                {
	                    int lowestClients =100000;
	                    int lowestID = 1000000;
	                    boolean same = false;
	                    for(int i = 0; i<DCproject2Server.servers.size();++i)
	                    {    //for all servers
	                        if(DCproject2Server.servers.get(i).idNum!=DCproject2Server.serverID) //if its not me
	                        {
	                            if(DCproject2Server.servers.get(i).numClients<lowestClients)
	                            {   //do i have the lowest clients
	                                lowestClients = DCproject2Server.servers.get(i).numClients;
	                                same = false;
	                            }
	                            if(DCproject2Server.servers.get(i).numClients==lowestClients)
	                            {
	                                same=true;
	                            }

	                            if(DCproject2Server.servers.get(i).idNum<lowestID)
	                            {   //do i have the lowest clients
	                                lowestID = DCproject2Server.servers.get(i).idNum;
	                            }
	                        }
	                    }
	                    //if we have the lowest number
	                    if(DCproject2Server.numClients==lowestClients)
	                    {
	                        if(same)//but someone else has the same number
	                        {
	                            if(lowestID==DCproject2Server.serverID)//but we have the lower ID number!
	                            {
	                                myaddy = "%%%";
	                                byte[] sendData = myaddy.getBytes(); // make a packet that is the correct response of %%%
	                                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,packet.getAddress(),packet.getPort());
	                                socket.send(sendPacket); //send response to client
	                                System.out.println("Responded with "+myaddy);
	                                DCproject2Server.numClients++;
	                            }
	                        }
	                    }
	                }
	                else  //when we received other data in error
	                {
	                    if(message.equals("@@@"))
	                    {
	                        myaddy = "%%%"+DCproject2Server.serverID+":"+DCproject2Server.numClients;
	                        byte[] sendData = myaddy.getBytes(); // make a packet that is the correct response of %%%
	                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,packet.getAddress(),packet.getPort());
	                        socket.send(sendPacket); //send response to client
	                       // System.out.println("Responded with "+myaddy);
	                    }
	                    else
	                        System.out.println("not sure what happened there we recieved :"+message);

	                }
	            }
	        }
	        catch(IOException except) //rare occurance of problems output them to console
	        {
	            System.out.println("Something went wrong: "+except);
	        }
	    }
	    
	    private BCthreadHolder threadHolder;
	    
	    public BCthread getInstance()
	    {
	    	if(threadHolder == null) {
	    		threadHolder = new BCthreadHolder();
	    	}
	    	
	        return threadHolder.getInstance();
	    }
	    
	    private class BCthreadHolder
	    {
	    	private BCthread INSTANCE;
	    	
	    	public BCthreadHolder() {
	    		INSTANCE = BCthread.this;
	    	}
	    	
	    	public BCthread getInstance() {
	    		if(INSTANCE == null) {
	    			new BCthreadHolder();
	    		}
	    		
	    		return INSTANCE;
	    	}
	    	
	        
	    }

	}
}
