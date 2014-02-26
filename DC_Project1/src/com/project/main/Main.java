package com.project.main;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Scanner;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import com.project.framework.Task;
import com.project.server.DCServer;
import com.project.server.DCServer.COMMAND_TYPE;
import com.project.server.RoutingTableServlet;
import com.project.server.ServerReceiverServlet;
import com.project.server.router.Client;
import com.project.tasks.SendStringMessageTask;
import com.project.tasks.SimpleAbstractTask;
import com.project.tasks.TaskManager;
import com.project.tasks.ThreadHelper;
import com.project.ui.MainWindow;

/**
 * Created by Alex on 1/15/14.
 */

// I am thinking we have this as the "client/server" as it will be easy to port
// to all platforms
// the server/router we will have as a different class

// feel free to add classes with things you are working on, lets try and keep
// these main two files
// with "working code" so they will compile

public class Main {
	
	
	
	
	

//	public class ClientTask extends SimpleAbstractTask {
//
//        @Override
//        public void executeTask() {
//            // TODO Auto-generated method stub
//            /*
//            the following code will connect to the server router and then open a file for sending the data
//         */
//
//            try {
//                Socket clientSocket = new Socket(IPaddress, 6666); //port needs to be serverrouters port
//                //network output stream
//                DataOutputStream send = new DataOutputStream(clientSocket.getOutputStream());
//                //network input stream
//                BufferedReader receive = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                //get a lowercase message from the user (no verification or anything)
//                String message = fileName.getText();
//                
//                InputStream file = new FileInputStream(fileName.getText());
//                BufferedReader reader = new BufferedReader(new InputStreamReader(file));
//                String line = null;
//                
//                
//                /*
//            in the while loop below we need to add the statistics for average length of each line we send
//            the average round trip time of each message.
//                 */
//                
//                while ((line = reader.readLine()) != null)      //loop to end of file sending every line
//                {
//                    outputWindow.append("Sending TCP: " + line + "\n"); //we may want to only output statistics if its a long file
//                    //convert to bytes and write to stream
//                    send.writeBytes(line + '\n');
//                    //receive the message back from the server
//                    String modifiedMsg = receive.readLine();
//                    outputWindow.append("Server TCP: " + modifiedMsg + "\n");
//                    //we are done here close the socket!
//                    
//                }
//                clientSocket.close(); //we are done here
//                
//                
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void onProgressUpdate() {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onFinished() {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public byte[] toBytes() {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        @Override
//        public Task fromBytes(byte[] byteArray) {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//    }

    public class ServerListeningTask extends SimpleAbstractTask {

        @Override
        public void executeTask() {
            // TODO Auto-generated method stub
            try {
                ServerSocket Socket = new ServerSocket(6666); //port here can be anything as long as server router knows what it is
                
                /*
                We might want to consider adding a timeout feature to the server as it will hang forever waiting for a connection...
                we could also spawn a server thread to handle everything so as not to lock the gui. we can worry about this
                when the rest is functioning.
                 */
                
                while(serverSelect.isSelected()) //servers run loop forever
                {
                    //accept new connections to the socket (blocks forever)
                    outputWindow.append("Waiting for client on port  :" + Socket.getLocalPort() + "...\n");
                    Socket connectionSocket = Socket.accept();
                    outputWindow.append("Client connected on port    :" + Socket.getLocalPort() + "...\n");
                    //create streams
                    BufferedReader receive = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    DataOutputStream send = new DataOutputStream(connectionSocket.getOutputStream());
                    //get a new message from the socket
                    String message = receive.readLine();
                    //modify the sentence
                    String modifiedMsg = message.toUpperCase() + '\n';
                    //send modified message back
                    send.writeBytes(modifiedMsg);
                }
                
                
            } catch (IOException e) {
                
            }
        }

        @Override
        public void onProgressUpdate() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onFinished() {
            // TODO Auto-generated method stub

        }

        @Override
        public byte[] toBytes() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Task fromBytes(byte[] byteArray) {
            // TODO Auto-generated method stub
            return null;
        }

    }
    
    public static void doTempTest() throws UnknownHostException {
    	Scanner input = new Scanner(System.in);
    	System.out.print("Are you a SERVER or CLIENT? (C/S): " );
    	String userInput = input.nextLine();
		
		Client selfClient = new Client();
		selfClient.setCurrentIP(InetAddress.getLocalHost()
				.getHostAddress());
		selfClient.setHostname(InetAddress.getLocalHost()
				.getHostName());
		selfClient.setPort(ServerReceiverServlet.LISTENING_PORT);
		selfClient.setUsername("Client "
				+ DCServer.getLocalHostname());
		selfClient.SERVER_COMMAND = COMMAND_TYPE.ROUTE_DATA_TO_SERVER;
		selfClient.ROUTERTABLE_COMMAND = COMMAND_TYPE.PING_NODE;
    	
    	if(userInput.equalsIgnoreCase("C")) {
    		System.out.print("Recipient IP: " );
        	userInput = input.nextLine();
        	
        	selfClient.setDestinationIP(userInput);
    		
    		TaskManager.DoTask(new SendStringMessageTask(selfClient, true));
    	}
    	else {
    		//RoutingTable
    		TaskManager.DoTask(new ServerReceiverServlet());
    		TaskManager.DoTask(new RoutingTableServlet());
    	}
    }

    public static final void main(String[] args) {
    	
    	
    	
    	try {
			doTempTest();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
//        JFrame frame = new JFrame("baseClient");
//        frame.setContentPane(new Main().mains);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setVisible(true);
		
//		MainWindow mainUI = new MainWindow();
		
//		TaskManager.DoPersistentTask(new DCServer(), new ITaskCallback() {
//
//			@Override
//			public void onTaskStart(Task task) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void onAtomicTaskStart(Task task) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void onTaskProgress(Task task) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void onTaskFinished(Task task) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		});
		
//		ThreadHelper.sleepThread(2000);
		
//		System.out.println("Beginning other tasks.");
		
//		TaskManager.DoTask(new ServerFinderTask());
		
//		doTests();c
	}
	
    private JButton startButton;
    private JFormattedTextField ipAddress;
    private JTextArea outputWindow;
    private JFormattedTextField fileName;
    private JRadioButton clientSelect;
    private JRadioButton serverSelect;
    private JPanel mains;

    String IPaddress = "10.22.8.248"; //this needs to be the ip of the server router


	public static void doTests() {
		for(int i = 0; i < 50; i++) {
			TaskManager.DoTask(new SimpleAbstractTask("Task - " + i) {
				
				Random random = new Random();

				@Override
				public void executeTask() {
					for (int j = 0; j < 5; j++) {
						System.out.println("HELLO FROM TEST TASK: " + getTaskId());
						ThreadHelper.sleepThread(random.nextInt(1800) + 200);
					}
					
					stopTask();
				}

				@Override
				public void onProgressUpdate() {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onFinished() {
					System.out.println("TEST TASK: " + getTaskId() + " is finished");
				}

				@Override
				public byte[] toBytes() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public Task fromBytes(byte[] byteArray) {
					// TODO Auto-generated method stub
					return null;
				}
				
			});
		}
	}

    private void createUIComponents() {

    }

    public Main() {
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (serverSelect.isSelected()) {
                    TaskManager.DoTask(new ServerListeningTask());
                }
                if (clientSelect.isSelected())
                {
//                    TaskManager.DoTask(new ClientTask());
                }
            }
        });


    }





//nothing below here needs editing its all auto generated GUI data by intellij






    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mains = new JPanel();
        mains.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        mains.setMinimumSize(new Dimension(443, 500));
        mains.setPreferredSize(new Dimension(443, 500));
        fileName = new JFormattedTextField();
        fileName.setEditable(true);
        fileName.setText("");
        fileName.setToolTipText("Enter the name of a text file inthe same directory");
        mains.add(fileName, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), new Dimension(500, 40), 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mains.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, true));
        outputWindow = new JTextArea();
        outputWindow.setEditable(false);
        panel1.add(outputWindow, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(410, 400), new Dimension(410, 400), new Dimension(410, 400), 0, false));
        serverSelect = new JRadioButton();
        serverSelect.setLabel("Server Mode");
        serverSelect.setSelected(true);
        serverSelect.setText("Server Mode");
        mains.add(serverSelect, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(120, 40), new Dimension(120, 40), new Dimension(120, 40), 0, false));
        startButton = new JButton();
        startButton.setHideActionText(true);
        startButton.setHorizontalAlignment(0);
        startButton.setText("Start");
        startButton.setToolTipText("Click to start program");
        mains.add(startButton, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(80, 30), new Dimension(80, 30), new Dimension(80, 30), 0, false));
        clientSelect = new JRadioButton();
        clientSelect.setActionCommand("RadioButton");
        clientSelect.setLabel("Client Mode");
        clientSelect.setSelected(false);
        clientSelect.setText("Client Mode");
        mains.add(clientSelect, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(120, 40), new Dimension(120, 40), new Dimension(120, 40), 0, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(clientSelect);
        buttonGroup.add(serverSelect);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mains;
    }
}
