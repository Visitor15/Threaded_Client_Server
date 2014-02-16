package com.project.main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.project.framework.Task;
import com.project.server.DCServer;
import com.project.server.router.Client;
import com.project.tasks.ITaskCallback;
import com.project.tasks.SimpleTask;
import com.project.tasks.TaskManager;

/**
 * Created by Alex on 1/15/14.
 */

// I am thinking we have this as the "client/server" as it will be easy to port
// to all platforms
// the server/router we will have as a different class

// feel free to add classes with things you are working on, lets try and keep
// these main two files
// with "working code" so they will compile

public class Main implements ITaskCallback {

	public static final void main(String[] args) {
		DCServer.GET_INSTANCE().start();
		
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
//		int count = 0;
//		
//		do {
//			TaskManager.DO_TASK(new SimpleTask(Integer.toString(count)) {
//
//				@Override
//				public void execute() {
//					for (int i = 0; i < 10; i++) {
//						System.out.println("Posting from task " + getTaskId());
//						
//						try {
//							Thread.sleep(i * 10);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//					
//					stopTask();
//				}
//
//				@Override
//				public void onProgressUpdate() {
//					// TODO Auto-generated method stub
//					
//				}
//
//				@Override
//				public void onFinished() {
//					// TODO Auto-generated method stub
//					
//				}
//				
//			});
//			
//			
////			try {
////				Thread.sleep(500);
////			} catch (InterruptedException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
//			
//			count++;
//			
//		} while (count < 4);
//		
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		TaskManager.DO_TASK(new SimpleTask("I AM A SIMPLE CLASS!") {
//
//			@Override
//			public void execute() {
//				System.out.println("HELL FROM MEEEEEE");
//				
//				stopTask();
//			}
//
//			@Override
//			public void onProgressUpdate() {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void onFinished() {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		});

		int count = 0;
		boolean success;
//		do {
//			count++;
//			System.out.println("lalalal");
//			success = TaskManager.DO_TASK(new SimpleTask("MESSAGE TASK") {
//
//				@Override
//				public void execute() {
//					
////					for (int i = 0; i < 50; i++) {
//						try {
//							
////							Thread.sleep(5000);
//							
//							
//							Socket sock = new Socket("T520", DCServer
//											.GET_DEF_PORT());
//							
//							
//							Scanner keyboard = new Scanner(System.in);
//							System.out.println("Message: ");
//							String mMessage = keyboard.nextLine();
//							
//							
//
//							Client client = new Client(sock.getInetAddress()
//									.getHostAddress(), sock.getLocalPort());
//							
//							client.addStringMessage(mMessage);
//							
//							keyboard.close();
//
//							PrintWriter out = new PrintWriter(sock
//									.getOutputStream(), true);
//							BufferedReader in = new BufferedReader(
//									new InputStreamReader(sock.getInputStream()));
//
//							out.println(mMessage + " : ");
//
//							String fromServer = "null";
//							while ((fromServer = in.readLine()) != null) {
//								switch (Integer.valueOf(fromServer)) {
//								case 200: {
//									System.out.println("I registered!");
//									break;
//								}
//								case 401: {
//									System.out
//											.println("Authentication exception");
//									break;
//								}
//								default: {
//									System.out.println("Unknown Exception");
//									break;
//								}
//								}
//							}
//
//							out.flush();
//
//							in.close();
//							out.close();
//							sock.close();
//
////							Thread.sleep(3000);
//
//						} catch (UnknownHostException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
////					}
//
//				}
//
//				@Override
//				public void onProgressUpdate() {
//					// TODO Auto-generated method stub
//
//				}
//
//				@Override
//				public void onFinished() {
//					// TODO Auto-generated method stub
//
//				}
//
//			});
//
//		} while (count < 2);
	}

	@Override
	public void onTaskFinished(Task task) {
		System.out.println(this.getClass().getSimpleName() + " finished");
	}

	@Override
	public void onTaskStart(Task task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAtomicTaskStart(Task task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTaskProgress(Task task) {
		// TODO Auto-generated method stub
		
	}
}
