package com.project.framework;

import com.project.tasks.ITaskCallback;

public interface Task {
	
	public void setTaskId(String id);
	
	public String getTaskId();

	public void execute();
	
	public void beginTask(ITaskCallback callback);
	
	public void beginAtomicTask(ITaskCallback callback);
	
	public void stopAtomicTask();
	
	public void stopTask();
	
	public boolean isExecuting();
	
	public void onProgressUpdate();
	
	public void onFinished();
	
}
