package com.project.thread;

import java.util.List;

import com.project.framework.Task;
import com.project.thread.DCThread.THREAD_STATE;

public interface IDCThread {

	public void addBatchTasks(List<Task> taskList);

	public void addTask(Task task);

	void executeCurrentTasks();

	public THREAD_STATE getThreadState();

	public boolean isBusy();

	// public void idleThread();

	public void onFinished();

	public void onProgressUpdate();

	public void setCallback(IThreadPoolCallback taskCallback);

	public void setThreadState(THREAD_STATE state);

	public void startThread(boolean isAtomicOperation);

	public void stopThread();
}
