package com.project.ui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

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

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.project.dc_client.ClientSenderHandlerTask;
import com.project.dc_client.ClientTask;
import com.project.dc_client.ClientUpdaterTask;
import com.project.framework.Task;
import com.project.tasks.ITaskCallback;
import com.project.tasks.SimpleAbstractTask;
import com.project.tasks.SimplePersistentTask;
import com.project.tasks.TaskManager;

public class ClientGUITask extends SimplePersistentTask implements
		ITaskCallback {

	private JTextField portInput;
	public JList fileList;
	private JButton retrieveButton;
	private JPanel mainPanel;
	private JTextField directoryName;
	private JLabel Directory;
	private JButton Update;
	public JList outputWindow;
	private JProgressBar RBar;
	public JLabel serverGuiIP;
	public static String dirName;
	public static int port;
	public static ArrayList files;
	public static ArrayList myFiles;
	public static String serverAddy;
	public static boolean clientSend;
	public static boolean clientSenderExists;
	DefaultListModel listModel;
	DefaultListModel myListModel;

	public ClientGUITask() {
		setTaskId("Client UI");
	}

	private void init() {
		port = 8888;
		clientSend = true;
		files = new ArrayList();
		myFiles = new ArrayList();
		clientSenderExists = false;
		JFrame frame = new JFrame("clientNodeGui");
		frame.setContentPane(mainPanel);
		frame.setDefaultCloseOperation(3);
		frame.pack();
		frame.setVisible(true);
		System.out.println("booting gui");

		TaskManager.DoTask(new ClientTask());
		TaskManager.DoTask(new ClientSenderHandlerTask());

		// clientbackend BEnd = new clientbackend();
		// BEnd.start();
		// clientSenderHandler CSH = new clientSenderHandler();
		// CSH.start();
	}

	@Override
	public void executeTask() {
		createUIComponents();
		$$$setupUI$$$();
		init();
		
		
		
		
		
		
		
		
		Update.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				Update.setEnabled(false);
				retrieveButton.setEnabled(false);
				try {
					if (ClientGUITask.serverAddy != null)
						serverGuiIP.setText((new StringBuilder())
								.append(ClientGUITask.serverAddy)
								.append(":6666").toString());
					ClientGUITask.clientSend = false;
					getFileList();
				} catch (Exception excep) {
					System.out.println((new StringBuilder())
							.append("we failed to get file list :")
							.append(excep).toString());
				}
			}

		});
		retrieveButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					Update.setEnabled(false);
					retrieveButton.setEnabled(false);
					if (!fileList.isSelectionEmpty())
						getFileHandler();
				} catch (Exception excep) {
					System.out.println((new StringBuilder())
							.append("failed to retrieve file :").append(excep)
							.toString());
				}
			}

		});
		directoryName.addFocusListener(new FocusListener() {

			public void focusLost(FocusEvent arg0) {
				if (directoryName.getText().charAt(
						directoryName.getText().length() - 1) != '/')
					directoryName.setText((new StringBuilder())
							.append(directoryName.getText()).append("/")
							.toString());
			}

			public void focusGained(FocusEvent focusevent) {
			}

		});

//		fileList.setEnabled(false);
//		System.out.println((new StringBuilder()).append("Requesting ")
//				.append(fileList.getSelectedValue().toString())
//				.append(" from the server").toString());
//		String hostIP = "";
//		int hostPort = 8888;
//		try {
//			byte senddata[] = fileList.getSelectedValue().toString().getBytes();
//			DatagramSocket clientN = new DatagramSocket();
//			clientN.setSoTimeout(10000);
//			try {
//				DatagramPacket sendPacket = new DatagramPacket(senddata,
//						senddata.length,
//						InetAddress.getByName(ClientGUITask.serverAddy), 6666);
//				clientN.send(sendPacket);
//				System.out.println("Requested file");
//			} catch (Exception excep) {
//				System.out.println("Error in file request");
//			}
//			byte recBuffer[] = new byte[15000];
//			DatagramPacket receivePacket = new DatagramPacket(recBuffer,
//					recBuffer.length);
//			clientN.receive(receivePacket);
//			String temp = (new String(receivePacket.getData())).trim();
//			String parts[] = temp.split(":");
//			hostIP = parts[0];
//			hostPort = Integer.parseInt(parts[1]);
//			System.out.println((new StringBuilder())
//					.append("File is hosted at ").append(hostIP).append(":")
//					.append(hostPort).toString());
//			clientN.close();
//		} catch (Exception excep) {
//			System.out.println("failed on something major");
//		}
//		try {
//			Socket socket = new Socket(InetAddress.getByName(hostIP), hostPort);
//			System.out.println((new StringBuilder()).append("We connected to ")
//					.append(socket.getInetAddress().getHostName()).append(":")
//					.append(socket.getPort()).toString());
//			OutputStream outstreams = socket.getOutputStream();
//			String fileWanted = (new StringBuilder())
//					.append(fileList.getSelectedValue().toString())
//					.append("\n").toString();
//			outstreams.write(fileWanted.getBytes());
//			System.out.println((new StringBuilder())
//					.append("we asked the host for ")
//					.append(fileList.getSelectedValue().toString()).toString());
//			byte buffer[] = new byte[1024];
//			DataInputStream inData = new DataInputStream(
//					socket.getInputStream());
//			RBar.setStringPainted(true);
//			long fileSize = inData.readLong();
//			RBar.setMaximum((int) fileSize);
//			File myFile = new File((new StringBuilder())
//					.append(directoryName.getText())
//					.append(fileList.getSelectedValue().toString()).toString());
//			FileOutputStream fos = new FileOutputStream(myFile);
//			int count;
//			while ((count = inData.read(buffer)) > 0) {
//				fos.write(buffer, 0, count);
//				RBar.setValue(RBar.getValue() + count);
//			}
//			System.out.println("we actually got here!");
//			fos.close();
//			outstreams.close();
//			inData.close();
//			socket.close();
//		} catch (Exception excep) {
//			System.out
//					.println((new StringBuilder())
//							.append("file recieving error : ").append(excep)
//							.toString());
//		}
//		getFileList();
//		RBar.setValue(0);
//		RBar.setStringPainted(false);
//		fileList.setEnabled(true);
//		Update.setEnabled(true);
//		retrieveButton.setEnabled(true);
		
		
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

	private void createUIComponents() {
		listModel = new DefaultListModel();
		myListModel = new DefaultListModel();
		outputWindow = new JList(myListModel);
		fileList = new JList(listModel);
	}

	void getFileList() {
		fileList.setEnabled(false);
		port = Integer.parseInt(portInput.getText());
		dirName = directoryName.getText();
		// clientUpdaterThread CU = new clientUpdaterThread();
		// CU.start();

		try {
			TaskManager.DoTaskOnCurrentThread(new ClientUpdaterTask(),
					ClientGUITask.this);

			// CU.join();
		} catch (Exception e) {
			System.out.println((new StringBuilder())
					.append("Error waiting on the client updater : ").append(e)
					.toString());
		}
		fileList.clearSelection();
		listModel.removeAllElements();
		myListModel.removeAllElements();
		for (int i = 0; i < files.size(); i++)
			if (!myFiles.contains(files.get(i)))
				listModel.addElement(files.get(i));

		for (int i = 0; i < myFiles.size(); i++)
			myListModel.addElement(myFiles.get(i));

		clientSend = true;
		fileList.setEnabled(true);
		Update.setEnabled(true);
		retrieveButton.setEnabled(true);
	}

	void getFileHandler() {
		fileList.setEnabled(false);
		System.out.println((new StringBuilder()).append("Requesting ")
				.append(fileList.getSelectedValue().toString())
				.append(" from the server").toString());

		TaskManager.DoTask(new ClientReceiverTask());

		// Thread getter = new Thread(new clientReciever());
		// getter.start();
	}

	private void $$$setupUI$$$() {
		
		JPanel jpanel = new JPanel();
		mainPanel = jpanel;
		jpanel.setLayout(new GridLayoutManager(8, 4, new Insets(0, 0, 0, 0),
				-1, -1, false, false));
		jpanel.setName("");
		JLabel jlabel = new JLabel();
		jlabel.setText("PORT NUMBER");
		jpanel.add(jlabel, new GridConstraints(2, 0, 1, 1, 8, 0, 0, 0, null,
				null, null));
		JTextField jtextfield = new JTextField();
		directoryName = jtextfield;
		jtextfield.setText("c:/");
		jpanel.add(jtextfield, new GridConstraints(1, 1, 1, 2, 8, 1, 6, 0,
				null, new Dimension(150, -1), null));
		JLabel jlabel1 = new JLabel();
		Directory = jlabel1;
		jlabel1.setText("DIRECTORY");
		jpanel.add(jlabel1, new GridConstraints(1, 0, 1, 1, 8, 0, 0, 0, null,
				null, null));
		JTextField jtextfield1 = new JTextField();
		portInput = jtextfield1;
		jtextfield1.setText("8888");
		jpanel.add(jtextfield1, new GridConstraints(2, 1, 1, 2, 8, 1, 6, 0,
				null, new Dimension(150, -1), null));
		JButton jbutton = new JButton();
		Update = jbutton;
		jbutton.setToolTipText("retrieve uptodate file list of files you dont have");
		jbutton.setText("Update");
		jpanel.add(jbutton, new GridConstraints(3, 0, 1, 1, 0, 1, 3, 0, null,
				null, null));
		JButton jbutton1 = new JButton();
		retrieveButton = jbutton1;
		jbutton1.setText("Retrieve");
		jpanel.add(jbutton1, new GridConstraints(3, 1, 1, 2, 0, 1, 3, 0, null,
				null, null));
		JLabel jlabel2 = new JLabel();
		jlabel2.setText("My Files");
		jpanel.add(jlabel2, new GridConstraints(5, 0, 1, 1, 8, 0, 0, 0, null,
				null, null));
		JScrollPane jscrollpane = new JScrollPane();
		jpanel.add(jscrollpane, new GridConstraints(4, 1, 1, 2, 0, 3, 7, 7,
				null, null, null));
		JList jlist = fileList;
		jscrollpane.setViewportView(jlist);
		JLabel jlabel3 = new JLabel();
		jlabel3.setText("Remote Files");
		jpanel.add(jlabel3, new GridConstraints(4, 0, 1, 1, 8, 0, 0, 0, null,
				null, null));
		JScrollPane jscrollpane1 = new JScrollPane();
		jpanel.add(jscrollpane1, new GridConstraints(5, 1, 1, 2, 0, 3, 7, 7,
				null, null, null));
		JList jlist1 = outputWindow;
		jlist1.setEnabled(false);
		jscrollpane1.setViewportView(jlist1);
		JProgressBar jprogressbar = new JProgressBar();
		RBar = jprogressbar;
		jprogressbar.setStringPainted(false);
		jprogressbar.setIndeterminate(false);
		jpanel.add(jprogressbar, new GridConstraints(6, 1, 1, 2, 0, 1, 6, 0,
				null, null, null));
		Spacer spacer = new Spacer();
		jpanel.add(spacer, new GridConstraints(7, 1, 1, 2, 0, 2, 1, 6, null,
				null, null));
		Spacer spacer1 = new Spacer();
		jpanel.add(spacer1, new GridConstraints(2, 3, 1, 1, 0, 1, 6, 1, null,
				null, null));
		Spacer spacer2 = new Spacer();
		jpanel.add(spacer2, new GridConstraints(0, 2, 1, 1, 0, 2, 1, 6, null,
				null, null));
		JLabel jlabel4 = new JLabel();
		jlabel4.setText("Server :");
		jpanel.add(jlabel4, new GridConstraints(0, 0, 1, 1, 8, 0, 0, 0, null,
				null, null));
		JLabel jlabel5 = new JLabel();
		serverGuiIP = jlabel5;
		jlabel5.setText("");
		jpanel.add(jlabel5, new GridConstraints(0, 1, 1, 1, 8, 0, 0, 0, null,
				null, null));
	}

	public JComponent $$$getRootComponent$$$() {
		return mainPanel;
	}

	@Override
	public void onAtomicTaskStart(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskFinished(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskProgress(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskStart(Task task) {
		// TODO Auto-generated method stub

	}

	public class ClientReceiverTask extends SimpleAbstractTask {

		public ClientReceiverTask() {
			setTaskId("ClientReceiverTask");
		}

		@Override
		public void executeTask() {
			fileList.setEnabled(false);
			System.out.println((new StringBuilder()).append("Requesting ")
					.append(fileList.getSelectedValue().toString())
					.append(" from the server").toString());
			String hostIP = "";
			int hostPort = 8888;
			try {
				byte senddata[] = fileList.getSelectedValue().toString()
						.getBytes();
				DatagramSocket clientN = new DatagramSocket();
				clientN.setSoTimeout(10000);
				try {
					DatagramPacket sendPacket = new DatagramPacket(senddata,
							senddata.length,
							InetAddress.getByName(ClientGUITask.serverAddy),
							6666);
					clientN.send(sendPacket);
					System.out.println("Requested file");
				} catch (Exception excep) {
					System.out.println("Error in file request");
				}
				byte recBuffer[] = new byte[15000];
				DatagramPacket receivePacket = new DatagramPacket(recBuffer,
						recBuffer.length);
				clientN.receive(receivePacket);
				String temp = (new String(receivePacket.getData())).trim();
				String parts[] = temp.split(":");
				hostIP = parts[0];
				hostPort = Integer.parseInt(parts[1]);
				System.out.println((new StringBuilder())
						.append("File is hosted at ").append(hostIP)
						.append(":").append(hostPort).toString());
				clientN.close();
			} catch (Exception excep) {
				System.out.println("failed on something major");
			}
			try {
				Socket socket = new Socket(InetAddress.getByName(hostIP),
						hostPort);
				System.out.println((new StringBuilder())
						.append("We connected to ")
						.append(socket.getInetAddress().getHostName())
						.append(":").append(socket.getPort()).toString());
				OutputStream outstreams = socket.getOutputStream();
				String fileWanted = (new StringBuilder())
						.append(fileList.getSelectedValue().toString())
						.append("\n").toString();
				outstreams.write(fileWanted.getBytes());
				System.out.println((new StringBuilder())
						.append("we asked the host for ")
						.append(fileList.getSelectedValue().toString())
						.toString());
				byte buffer[] = new byte[1024];
				DataInputStream inData = new DataInputStream(
						socket.getInputStream());
				RBar.setStringPainted(true);
				long fileSize = inData.readLong();
				RBar.setMaximum((int) fileSize);
				File myFile = new File((new StringBuilder())
						.append(directoryName.getText())
						.append(fileList.getSelectedValue().toString())
						.toString());
				FileOutputStream fos = new FileOutputStream(myFile);
				int count;
				while ((count = inData.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
					RBar.setValue(RBar.getValue() + count);
				}
				System.out.println("we actually got here!");
				fos.close();
				outstreams.close();
				inData.close();
				socket.close();
			} catch (Exception excep) {
				System.out.println((new StringBuilder())
						.append("file recieving error : ").append(excep)
						.toString());
			}
			getFileList();
			RBar.setValue(0);
			RBar.setStringPainted(false);
			fileList.setEnabled(true);
			Update.setEnabled(true);
			retrieveButton.setEnabled(true);
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
}
