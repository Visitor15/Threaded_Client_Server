package com.project.ui;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.project.framework.Task;
import com.project.server.DCServer;
import com.project.server.router.RoutingTable;
import com.project.tasks.ITaskCallback;
import com.project.tasks.TaskManager;

public class MainWindow extends JFrame implements ITaskCallback {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7729924636158204916L;
	JPanel serverPanel;
	JPanel clientPanel;

	JButton pingServerBtn;
	JButton pingClientBtn;

	JTextField textField;

	JLabel receivedTextField;

	public MainWindow() {
		this.setTitle("Synchronizer");
		this.setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());

		this.setLayout(new BorderLayout());

		serverPanel = new JPanel();
		clientPanel = new JPanel();

		pingServerBtn = new JButton("Ping Server");
		pingClientBtn = new JButton("Ping Client");
		textField = new JTextField();
		textField.setColumns(20);

//		TaskManager.DoPersistentTask(new DCServer(), this);

		initMainComponents();

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	private void initMainComponents() {
		serverPanel.setSize(this.getSize().width / 2, this.getSize().height);
		clientPanel.setSize(this.getSize().width / 2, this.getSize().height);
		serverPanel.add(pingClientBtn, BorderLayout.WEST);
		clientPanel.add(pingServerBtn);
		clientPanel.add(textField);
		this.add(serverPanel);
		this.add(clientPanel);
		serverPanel.setVisible(true);
		clientPanel.setVisible(true);

		pingServerBtn.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TaskManager.DoTask(new ServerFinderTask(MainWindow.this));
			}

		});

		// TaskManager.DoTask(new ServerFinderTask(this));
	}

	@Override
	public void onAtomicTaskStart(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskFinished(Task task) {
		pingServerBtn.setText("Ping finished - " + task.getTaskId());
		pingClientBtn.setText(RoutingTable.getInstance().getServerAtIndex(0)
				.getHostname());
	}

	@Override
	public void onTaskProgress(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskStart(Task task) {
		pingClientBtn.setText("...");
		pingServerBtn.setText("Pinging...");
	}
}
