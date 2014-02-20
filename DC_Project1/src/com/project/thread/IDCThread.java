package com.project.thread;

import java.util.List;

import com.project.framework.Task;
import com.project.thread.DCThread.THREAD_STATE;

public interface IDCThread {
	
	public void addTask(Task task);
	
	public void addBatchTasks(List<Task> taskList);
	
	void executeCurrentTasks();
	
	public void startThread(boolean isAtomicOperation);
	
	public void stopThread();
	
//	public void idleThread();
	
	public void onFinished();
	
	public void onProgressUpdate();
	
	public void setThreadState(THREAD_STATE state);
	
	public THREAD_STATE getThreadState();
	
	public boolean isBusy();
	
	public void setCallback(IThreadPoolCallback taskCallback);
}
