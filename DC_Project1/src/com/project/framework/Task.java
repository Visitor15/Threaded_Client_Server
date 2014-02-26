package com.project.framework;

import java.io.Serializable;

import com.project.tasks.ITaskCallback;

public interface Task extends Serializable {

	public void beginAtomicTask(ITaskCallback callback);

	public void beginTask(ITaskCallback callback);

	public void executeTask();

	public Task fromBytes(final byte[] byteArray);

	public String getStringData();

	public ITaskCallback getTaskCallback();

	public String getTaskId();

	public boolean isExecuting();

	public void logTask();

	public void onFinished();

	public void onProgressUpdate();

	public void setStringData(final String data);

	public void setTaskCallback(final ITaskCallback callback);

	public void setTaskId(String id);

	public void stopAtomicTask();

	public void stopTask();

	public byte[] toBytes();

}
