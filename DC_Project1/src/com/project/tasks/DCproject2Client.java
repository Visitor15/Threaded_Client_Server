package com.project.tasks;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.project.framework.Task;

public class DCproject2Client extends SimplePersistentTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3619003123311755586L;
	clientNodeGui clientGUI;

	public DCproject2Client() {
		System.out.println("Starting");
		setTaskId("DCproject2Client");
	}

	@Override
	public void executeTask() {
		System.out.println("BITCHES I AM STARTING!");
		do {
			if (clientGUI == null) {
				clientGUI = new clientNodeGui();
			}
		} while (isExecuting());

		stopTask();
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

	public class client extends Thread {
		protected boolean serverContinue = true;
		protected Socket socket;

		public client(Socket clientSoc) {
			socket = clientSoc;
			start();
		}

		public void run() {
			try {
				BufferedReader instream = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				OutputStream oustream = socket.getOutputStream();

				String temp = instream.readLine();
				System.out.println("trying to open a file called :"
						+ clientGUI.dirName + temp);
				File myFile = new File(clientGUI.dirName + temp);

				int count;
				byte[] buffer = new byte[1024];

				BufferedInputStream in = new BufferedInputStream(
						new FileInputStream(myFile));
				while ((count = in.read(buffer)) > 0) {
					oustream.write(buffer, 0, count);
					oustream.flush();
				}
				socket.close();
				oustream.close();
				in.close();
			} catch (IOException e) {
				System.err.println("Problems!");
				System.exit(1);
			}
		}
	}

	public class clientbackend extends Thread {

		public void run() {
			try {
				System.out.println("preparing UDP broadcast"); // debug output
																// to console
				byte[] senddata = "$$$".getBytes(); // prepare valid client data
													// for sending to server
				DatagramSocket clientN = new DatagramSocket(); // create a new
																// socket
				clientN.setBroadcast(true); // enable broadcasting

				try { // send to the highest order broadcast address
					DatagramPacket sendPacket = new DatagramPacket(senddata,
							senddata.length,
							InetAddress.getByName("255.255.255.255"), 6785);
					clientN.send(sendPacket);
					System.out.println("broadcast to : 255.255.255.255");
				} catch (Exception excep) // this should never fail
				{
					System.out
							.println("Failed to broadcast to : 255.255.255.255");
				}
				// next load all network interfaces into a list
				Enumeration<NetworkInterface> interfaces = NetworkInterface
						.getNetworkInterfaces();
				while (interfaces.hasMoreElements()) // for all network
														// interfaces
				{
					NetworkInterface networkInterface = interfaces
							.nextElement();
					if (networkInterface.isLoopback()
							|| !networkInterface.isUp())// if its a 127.0.0.1
														// (local address) or
														// not connected
						continue; // skip it
					for (InterfaceAddress interfaceAddress : networkInterface
							.getInterfaceAddresses()) {
						InetAddress broadcast = interfaceAddress.getBroadcast();
						if (broadcast == null) // if broadcast isnt allowed
							continue; // skip it
						try {
							DatagramPacket sendPacket = new DatagramPacket(
									senddata, senddata.length, broadcast, 6785);
							clientN.send(sendPacket); // send the packet to the
														// broadcast on all
														// valid interfaces
							System.out.println("broadcast to : " + broadcast);
						} catch (Exception excep) {
							System.out
									.println("Error broadcast to : rest of broadcast pools");
						}
					}
				}
				System.out.println("Finished broadcasting");
				byte[] recBuffer = new byte[15000];

				DatagramPacket receivePacket = new DatagramPacket(recBuffer,
						recBuffer.length);
				clientN.receive(receivePacket); // receive a response, hopefully
												// its from the server

				String temp = new String(receivePacket.getData()).trim(); // trim
																			// extra
																			// chars
																			// like
																			// \n

				if (temp.substring(0, 3).equals("%%%"))// if its a response from
														// a server.
				{
					clientGUI.serverAddy = receivePacket.getAddress()
							.getHostAddress(); // get the servers address from
												// the packet
					System.out.println("Found server at "
							+ clientGUI.serverAddy + ":6666"); // server uses
																// fixed port.
																// debug output
				} else {
					System.out.println("recieved : " + temp); // well what the
																// hell did we
																// recieve!?!
				}

				clientN.close(); // we should be done here
			} catch (Exception excep) {
				System.out.println("failed on something major");
			}

		}
	}

	public class clientNodeGui {
		private JTextField portInput; // port input on gui
		public JList fileList; // list of remote files on gui
		private JButton retrieveButton;
		private JPanel mainPanel;
		private JTextField directoryName; // the name of the directory to look
											// in (field on gui)
		private JLabel Directory;
		private JButton Update;
		public JList outputWindow; // the list of local files on gui
		private JProgressBar RBar;
		public JLabel serverGuiIP;

		public String dirName; // static reference to the directory chosen on
								// gui
		public int port; // static reference to the port chosen on gui

		public ArrayList<String> files; // remote files (Static)
		public ArrayList<String> myFiles; // local files (static)

		public String serverAddy; // the ipaddress of the server

		public boolean clientSend; // when we want the file server portion of
									// the gui running
		public boolean clientSenderExists;
		DefaultListModel<String> listModel = new DefaultListModel<String>(); // for
																				// adapting
																				// arraylists
																				// to
																				// jlists
		DefaultListModel<String> myListModel = new DefaultListModel<String>();

		private void createUIComponents() {
			System.out.println("Creating components");
			outputWindow = new JList(myListModel); // link jlist to list models
			fileList = new JList(listModel);
			// TODO: place custom component creation code here
		}

		public clientNodeGui() {
			System.out.println("Starting GUI");
			$$$setupUI$$$();
			// when the update button is pushed
			Update.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (!clientSenderExists) {
						clientSenderHandler CSH = new clientSenderHandler(); // start
																				// a
																				// file
																				// server
						CSH.start();
					}
					try {
						if (serverAddy != null)
							serverGuiIP.setText(serverAddy + ":6666");
						clientSend = false; // the file host should stop sending
						getFileList(); // we should get the list of files now

					} catch (Exception excep) {
						System.out.println("we failed to get file list :"
								+ excep);
					}
				}
			});
			// when the retrieve button is pushed
			retrieveButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						if (!fileList.isSelectionEmpty()) // if they have
															// something
															// selected
							getFileHandler(); // get the file!!
					} catch (Exception excep) {
						System.out.println("failed to retrieve file :" + excep);
					}
				}
			});

			directoryName.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(FocusEvent arg0) {

					if (directoryName.getText().charAt(
							directoryName.getText().length() - 1) != '/') // if
																			// the
																			// end
																			// of
																			// the
																			// string
																			// is
																			// not
																			// a
																			// /

					{
						directoryName.setText(directoryName.getText() + "/"); // add
																				// the
																				// /
																				// to
																				// the
																				// end
																				// will
																				// error
																				// out
																				// without
																				// it
					}

				}

				public void focusGained(FocusEvent arg0) {
				}
			});

			startGUI();
		}

		public void startGUI() {
			port = 8888; // default port
			clientSend = true; // client is ok to start a file server
			files = new ArrayList<String>(); // init local and remote file
												// listings
			myFiles = new ArrayList<String>();
			clientSenderExists = false;
			JFrame frame = new JFrame("clientNodeGui"); // start the gui
			frame.setContentPane(mainPanel);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);
			System.out.println("booting gui");

			clientbackend BEnd = new clientbackend(); // authenticate and
														// register with server
			BEnd.start();

		}

		void getFileList() {
			fileList.setEnabled(false); // lock the file list
			port = Integer.parseInt(portInput.getText()); // get the current
															// port number
			dirName = directoryName.getText(); // get the current directory
			clientUpdaterThread CU = new clientUpdaterThread(); // run the
																// update thread
			CU.start();
			try {
				CU.join();
			} catch (Exception e) {
				System.out
						.println("Error waiting on the client updater : " + e);
			}

			fileList.clearSelection(); // they dont have anything selected any
										// more

			listModel.removeAllElements(); // get rid of all gui listings
			myListModel.removeAllElements();

			for (int i = 0; i < files.size(); i++) { // get the new listing
				if (!myFiles.contains(files.get(i))) // if i dont have this file
														// then display it
					listModel.addElement(files.get(i));

			}

			for (int i = 0; i < myFiles.size(); i++)
				// list files i do have
				myListModel.addElement(myFiles.get(i));

			clientSend = true; // we are good to run the file server again
			fileList.setEnabled(true); // we can unlock the file list
		}

		void getFileHandler() {
			fileList.setEnabled(false); // (lock the file list) whole gui is
										// locked anyway
			System.out.println("Requesting "
					+ fileList.getSelectedValue().toString()
					+ " from the server");

			Thread getter = new Thread(new clientReciever());
			getter.start();

		}

		class clientReciever implements Runnable {
			public void run() {
				fileList.setEnabled(false); // (lock the file list) whole gui is
											// locked anyway
				System.out.println("Requesting "
						+ fileList.getSelectedValue().toString()
						+ " from the server");

				String hostIP = ""; // we use defaults until server gives us the
									// correct info
				int hostPort = 8888;

				try {
					byte[] senddata = fileList.getSelectedValue().toString()
							.getBytes(); // get file name user wants
					DatagramSocket clientN = new DatagramSocket();
					clientN.setSoTimeout(10000);

					try { // request the file from the server
						DatagramPacket sendPacket = new DatagramPacket(
								senddata, senddata.length,
								InetAddress.getByName(clientGUI.serverAddy),
								6666);
						clientN.send(sendPacket);
						System.out.println("Requested file");
					} catch (Exception excep) {
						System.out.println("Error in file request");
					}

					byte[] recBuffer = new byte[15000];

					DatagramPacket receivePacket = new DatagramPacket(
							recBuffer, recBuffer.length);
					clientN.receive(receivePacket); // get response from server

					String temp = new String(receivePacket.getData()).trim();

					// seperate the data recieved at the : (IP:port)

					String[] parts = temp.split(":");
					hostIP = parts[0]; // first half is a host IP address
					hostPort = Integer.parseInt(parts[1]); // second half is a
															// host port

					System.out.println("File is hosted at " + hostIP + ":"
							+ hostPort);

					clientN.close(); // close this connection
				} catch (Exception excep) {
					System.out.println("failed on something major");
				}

				try {
					Socket socket = new Socket(InetAddress.getByName(hostIP),
							hostPort); // connect to the host we were told about

					System.out.println("We connected to "
							+ socket.getInetAddress().getHostName() + ":"
							+ socket.getPort());

					OutputStream outstreams = socket.getOutputStream();

					String fileWanted = fileList.getSelectedValue().toString()
							+ "\n";

					// request the file from that host
					outstreams.write(fileWanted.getBytes());

					System.out.println("we asked the host for "
							+ fileList.getSelectedValue().toString());

					// for compatibility with binary files
					byte[] buffer = new byte[1024];

					DataInputStream inData = new DataInputStream(
							socket.getInputStream());

					RBar.setStringPainted(true);

					long fileSize = inData.readLong();
					RBar.setMaximum((int) fileSize);

					File myFile = new File(directoryName.getText()
							+ fileList.getSelectedValue().toString());
					FileOutputStream fos = new FileOutputStream(myFile);
					int count;

					while ((count = inData.read(buffer)) > 0) {
						fos.write(buffer, 0, count);
						RBar.setValue(RBar.getValue() + count);

					}

					System.out.println("we actually got here!");

					// we are done close everything
					fos.close();
					outstreams.close();
					inData.close();
					socket.close();
				} catch (Exception excep) {
					System.out.println("file recieving error : " + excep);
				}

				getFileList(); // update the server and the gui to show we have
								// the file!!
				RBar.setValue(0);
				RBar.setStringPainted(false);
				fileList.setEnabled(true);
			}

		}

		/**
		 * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO
		 * NOT edit this method OR call it in your code!
		 * 
		 * @noinspection ALL
		 */
		private void $$$setupUI$$$() {
			createUIComponents();
			System.out.println("Created com;alsdfjl;sdj");
			mainPanel = new JPanel();
			mainPanel
					.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(
							5, 2, new Insets(0, 0, 0, 0), -1, -1));
			final JLabel label1 = new JLabel();
			label1.setText("PORT NUMBER");
			mainPanel
					.add(label1,
							new com.intellij.uiDesigner.core.GridConstraints(
									1,
									0,
									1,
									1,
									com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
									com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
									com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
									com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
									null, null, null, 0, false));
			directoryName = new JTextField();
			directoryName.setText("c:/");
			mainPanel
					.add(directoryName,
							new com.intellij.uiDesigner.core.GridConstraints(
									0,
									1,
									1,
									1,
									com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
									com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
									com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
									com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
									null, new Dimension(150, -1), null, 0,
									false));
			Directory = new JLabel();
			Directory.setText("DIRECTORY");
			mainPanel
					.add(Directory,
							new com.intellij.uiDesigner.core.GridConstraints(
									0,
									0,
									1,
									1,
									com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
									com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
									com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
									com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
									null, null, null, 0, false));
			portInput = new JTextField();
			portInput.setText("8888");
			mainPanel
					.add(portInput,
							new com.intellij.uiDesigner.core.GridConstraints(
									1,
									1,
									1,
									1,
									com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
									com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
									com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
									com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
									null, new Dimension(150, -1), null, 0,
									false));
			Update = new JButton();
			Update.setText("Update");
			Update.setToolTipText("retrieve uptodate file list of files you dont have");
			mainPanel
					.add(Update,
							new com.intellij.uiDesigner.core.GridConstraints(
									2,
									0,
									1,
									1,
									com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
									com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
									com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
											| com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
									com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
									null, null, null, 0, false));
			retrieveButton = new JButton();
			retrieveButton.setText("Retrieve");
			mainPanel
					.add(retrieveButton,
							new com.intellij.uiDesigner.core.GridConstraints(
									2,
									1,
									1,
									1,
									com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
									com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
									com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
											| com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
									com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
									null, null, null, 0, false));
			final JLabel label2 = new JLabel();
			label2.setText("My Files");
			mainPanel
					.add(label2,
							new com.intellij.uiDesigner.core.GridConstraints(
									4,
									0,
									1,
									1,
									com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
									com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
									com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
									com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
									null, null, null, 0, false));
			final JScrollPane scrollPane1 = new JScrollPane();
			mainPanel
					.add(scrollPane1,
							new com.intellij.uiDesigner.core.GridConstraints(
									3,
									1,
									1,
									1,
									com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
									com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
									com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
											| com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
									com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
											| com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
									null, null, null, 0, false));
			scrollPane1.setViewportView(fileList);
			final JLabel label3 = new JLabel();
			label3.setText("Remote Files");
			mainPanel
					.add(label3,
							new com.intellij.uiDesigner.core.GridConstraints(
									3,
									0,
									1,
									1,
									com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
									com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
									com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
									com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
									null, null, null, 0, false));
			final JScrollPane scrollPane2 = new JScrollPane();
			mainPanel
					.add(scrollPane2,
							new com.intellij.uiDesigner.core.GridConstraints(
									4,
									1,
									1,
									1,
									com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
									com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
									com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
											| com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
									com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
											| com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
									null, null, null, 0, false));
			outputWindow.setEnabled(false);
			scrollPane2.setViewportView(outputWindow);

			System.out.println("Ending setupUI");
		}

		/**
		 * @noinspection ALL
		 */
		public JComponent $$$getRootComponent$$$() {
			return mainPanel;
		}
	}

	public class clientSender extends Thread {
		private Socket socket;

		public clientSender(Socket clientSoc) {
			socket = clientSoc;
			start();
		}

		public void run() // if we are here a client is connected and about to
							// request a file
		{
			try {
				BufferedReader instream = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));

				String temp = instream.readLine(); // get the name of the file
													// requested
				System.out.println("trying to open a file called :"
						+ clientGUI.dirName + temp);
				File myFile = new File(clientGUI.dirName + temp); // open the
																	// file they
																	// requested

				int count;
				byte[] buffer = new byte[1024];

				// input from the file and immediately output it out the TCP
				// socket

				// for compatibility with binary files

				DataOutputStream outData = new DataOutputStream(
						socket.getOutputStream());

				// send file size

				outData.writeLong(myFile.length());
				outData.flush();

				FileInputStream fis = new FileInputStream(myFile);

				while ((count = fis.read(buffer)) > 0) {
					outData.write(buffer, 0, count);
				}
				fis.close();
				outData.flush();
				outData.close();
			} catch (IOException e) {
				System.err.println("Problem sending file!");
			}

		}
	}

	public class clientSenderHandler extends Thread {
		ServerSocket fs;
		clientSender fileserver;

		public void run() {
			clientGUI.clientSenderExists = true;
			try {
				fs = new ServerSocket(clientGUI.port); // when we start grab the
														// port from the gui and
														// listen on it
			} catch (Exception e) {
				System.out.println("problem with initial file sender setup");
			}
			try {
				while (true) // forever
				{
					while (clientGUI.clientSend) // when we want the sender
													// working (disabled during
													// updates)
					{
						try {
							if (clientGUI.port != fs.getLocalPort()) // if the
																		// user
																		// changed
																		// the
																		// port
																		// in
																		// the
																		// gui
							{
								fs.close(); // close the old port
								System.out
										.println("Opened port "
												+ clientGUI.port
												+ " for sending files");
								fs = new ServerSocket(clientGUI.port); // open
																		// with
																		// new
																		// port
																		// number
							}
							fs.setSoTimeout(100000); // set a time out long
														// enough to send files
														// but short enough for
														// the gui to be
														// able to update
							fileserver = new clientSender(fs.accept()); // spawn
																		// a new
																		// thread
																		// when
																		// a
																		// client
																		// has
																		// connected
						} catch (Exception e) {
							System.out.println("Client sender handler : " + e);
							System.out
									.println("(handler times out to allow port change)");
						}
					}
				}
			} catch (Exception e) {
				System.out.println("But this should be impossible!");
			}
			clientGUI.clientSenderExists = false; // should never get here but
													// if it does and update
													// will make a new one
		}
	}

	public class clientUpdaterThread extends Thread {
		public void run() {
			File folder = new File(clientGUI.dirName); // in this directory
			File[] listOfFiles = folder.listFiles(); // get everyghing in the
														// directory
			int debug = 0; // had some issues with this on a slower computer
			ArrayList<String> filer; // list of files
			while (debug < 5) // occasionally had a file error always seems to
								// work within 5
			{
				try {
					clientGUI.myFiles.clear(); // get rid of our current list of
												// files locally
					clientGUI.files.clear(); // get rid of our current list of
												// remote files

					System.out.println("looking at " + clientGUI.dirName); // debug
																			// where
																			// are
																			// we
																			// looing?

					if (listOfFiles.length > 0) { // if there are files
						for (int i = 0; i < listOfFiles.length; i++) {
							if (listOfFiles[i].isFile()) { // if it is a file
															// and not a
															// directory
															// System.out.println("Found a file called "+listOfFiles[i].getName());
								if (!clientGUI.myFiles.contains(listOfFiles[i]
										.getName()))// if we dont already have
													// it in the list (we
													// shouldnt)
								{
									clientGUI.myFiles.add(listOfFiles[i]
											.getName());// add its name to the
														// list
								}
							}
						}
					}
					break;
				} catch (Exception excep) {
					debug++;
					System.out.println("problem with file listing retry :"
							+ debug + "out of 5");// output which debug number
													// if failed on
				}
			}
			try {

				Socket localSocket = new Socket(clientGUI.serverAddy, 6666); // connect
																				// to
																				// the
																				// servers
																				// tcp
																				// port

				localSocket.setSoTimeout(100000); // timeout incase something
													// goes wrong
				BufferedReader Istream = new BufferedReader(
						new InputStreamReader(localSocket.getInputStream()));
				OutputStream Ostream = localSocket.getOutputStream();

				String temp = "###" + clientGUI.port + "\n"; // tell the server
																// our current
																// port number!
				Ostream.write(temp.getBytes());

				for (int i = 0; i < clientGUI.myFiles.size(); ++i) // for all
																	// files
				{
					temp = clientGUI.myFiles.get(i) + "\n";
					Ostream.write(temp.getBytes()); // tell the server what
													// files i have
				}
				System.out.println("done Sending files...");
				temp = "EOF\n";
				Ostream.write(temp.getBytes()); // send EOF as escape sequence
												// to notify server thats the
												// end of the list

				System.out.println("sent EOF");

				while (true) // while the server still is sending files
				{
					temp = Istream.readLine(); // get what the server sends

					// System.out.println("received : "+temp);
					if (temp.equals("EOF")) // if its the end of files on the
											// server
					{
						localSocket.close(); // close everything
						Istream.close();
						break; // break the infinite loop
					} else {
						if (!clientGUI.files.contains(temp)) // if we dont have
																// that file in
																// the list (we
																// shouldnt)
						{
							clientGUI.files.add(temp); // add it to the list!!
						}
					}

				}
			} catch (Exception excep) {
			}
			System.out.println("Done with update");
		}
	}
}
