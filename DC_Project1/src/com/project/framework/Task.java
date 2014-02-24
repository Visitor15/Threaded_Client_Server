package com.project.framework;

import java.io.Serializable;

import com.project.tasks.ITaskCallback;

public interface Task extends Serializable {
	
	public void setTaskId(String id);
	
	public String getTaskId();
	
	public String getStringData();
	
	public void setStringData(final String data);

	public void executeTask();
	
	public void beginTask(ITaskCallback callback);
	
	public void beginAtomicTask(ITaskCallback callback);
	
	public void stopAtomicTask();
	
	public void stopTask();
	
	public boolean isExecuting();
	
	public void onProgressUpdate();
	
	public void onFinished();
	
	public ITaskCallback getTaskCallback();
	
	public void setTaskCallback(final ITaskCallback callback);
	
	public byte[] toBytes();
	
	public Task fromBytes(final byte[] byteArray);
	
}
