package com.project.main;
import com.project.framework.Task;
import com.project.server.DCServer;
import com.project.tasks.ITaskCallback;
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
		TaskManager.DoPersistentTask(new DCServer(), new ITaskCallback() {

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

			@Override
			public void onTaskFinished(Task task) {
				// TODO Auto-generated method stub
				
			}
			
		});
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
