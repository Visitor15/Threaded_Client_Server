package com.project.dc_server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.project.framework.Task;
import com.project.tasks.SimpleAbstractTask;
import com.project.tasks.TaskManager;

public class ClientConnectorTask extends SimpleAbstractTask {
	
	private DatagramSocket socket;  //listening socket

	public ClientConnectorTask() {
		
	}
	
	@Override
	public void executeTask() {
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
                    int lowestClients =DCServerTask.numClients;
                    int lowestID = DCServerTask.serverID;
                    boolean same = false;
                    for(int i = 0; i<DCServerTask.servers.size();++i)
                    {    //for all servers
                        if(DCServerTask.servers.get(i).idNum!=DCServerTask.serverID) //if its not me
                        {
                            if(DCServerTask.servers.get(i).numClients==lowestClients)
                            {
                                same=true;
                            }

                            if(DCServerTask.servers.get(i).idNum<lowestID)
                            {   //do i have the lowest clients
                                lowestID = DCServerTask.servers.get(i).idNum;
                            }
                            if(DCServerTask.servers.get(i).numClients<lowestClients)
                            {
                                lowestClients = DCServerTask.servers.get(i).numClients;
                            }
                        }
                    }
                    //if we have the lowest number
                    if(DCServerTask.numClients==lowestClients)
                    {
                        if(same)//but someone else has the same number
                        {
                            if(lowestID==DCServerTask.serverID)//but we have the lower ID number!
                            {
                                myaddy = "%%%";
                                byte[] sendData = myaddy.getBytes(); // make a packet that is the correct response of %%%
                                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,packet.getAddress(),packet.getPort());
                                socket.send(sendPacket); //send response to client
                                System.out.println("Responded with "+myaddy);
                                DCServerTask.numClients++;
                                
                                TaskManager.DoTask(new ServerLoadBalancerTask());

//                                serverLoadBalancer SLB = new serverLoadBalancer();//now update the number of clients we have.
//                                SLB.start();

                            }
                        }
                        else
                        {
                            myaddy = "%%%";
                            byte[] sendData = myaddy.getBytes(); // make a packet that is the correct response of %%%
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,packet.getAddress(),packet.getPort());
                            socket.send(sendPacket); //send response to client
                            System.out.println("Responded with "+myaddy);
                            DCServerTask.numClients++;
                            
                            TaskManager.DoTask(new ServerLoadBalancerTask());

//                            serverLoadBalancer SLB = new serverLoadBalancer();//now update the number of clients we have.
//                            SLB.start();

                        }

                    }
                }
                else  //when we received other data in error
                {
                    if(message.substring(0, 3).equals("@@@"))
                    {
                        ServerLoads tempSL = new ServerLoads();
                        String[] parts = message.substring(3,message.length()).split(":");
                        tempSL.idNum = Integer.parseInt(parts[0]);
                        tempSL.numClients = Integer.parseInt(parts[1]);
                        tempSL.serverIP = packet.getAddress().getHostAddress();

                        boolean existing = false;
                        for(int i = 0; i<DCServerTask.servers.size();i++)
                            if(DCServerTask.servers.get(i).idNum==tempSL.idNum) {
                            	DCServerTask.servers.get(i).numClients = tempSL.numClients;
                                existing = true;
                                break;
                            }
                        if(!existing)
                        {
                        	System.out.println("Adding server: " + tempSL);
                        	DCServerTask.servers.add(tempSL);
                        	TaskManager.DoTask(new ServerLoadBalancerTask());
                        	TaskManager.DoTask(new ServerFileQueryTask());
                        }
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

}
