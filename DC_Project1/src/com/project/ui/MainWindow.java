package com.project.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.project.framework.Task;
import com.project.server.DCServer;
import com.project.tasks.ITaskCallback;
import com.project.tasks.ServerFinderTask;
import com.project.tasks.TaskManager;

public class MainWindow extends JFrame implements ITaskCallback {
	
	JPanel serverPanel;
	JPanel clientPanel;
	
	JButton pingServerBtn;
	JButton pingClientBtn;

	public MainWindow() {
		this.setTitle("Synchronizer");
		this.setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());
		
		serverPanel = new JPanel();
		clientPanel = new JPanel();
		pingServerBtn = new JButton("Ping Server");
		pingClientBtn = new JButton("Ping Client");
		
		TaskManager.DoPersistentTask(new DCServer(), this);
		
		
		initMainComponents();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	private void initMainComponents() {
		serverPanel.add(pingClientBtn);
		clientPanel.add(pingServerBtn);
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
				TaskManager.DoTask(new ServerFinderTask(MainWindow.this));
			}
			
		});
		
//		TaskManager.DoTask(new ServerFinderTask(this));
	}

	@Override
	public void onTaskStart(Task task) {
		pingServerBtn.setText("Pinging...");
	}

	@Override
	public void onAtomicTaskStart(Task task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTaskProgress(Task task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTaskFinished(Task task) {
		pingServerBtn.setText("Ping finished - " + task.getTaskId());
	}
}
