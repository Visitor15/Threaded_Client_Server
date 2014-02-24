package com.project.main;
import java.util.Random;

import com.project.framework.Task;
import com.project.server.DCServer;
import com.project.tasks.ITaskCallback;
import com.project.tasks.ServerFinderTask;
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

	public static final void main(String[] args) {
		
		MainWindow mainUI = new MainWindow();
		
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
		
//		doTests();
	}
	
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
}
