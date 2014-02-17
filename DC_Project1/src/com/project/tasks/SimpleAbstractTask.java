package com.project.tasks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale.Category;

import com.project.framework.Task;

public abstract class SimpleAbstractTask implements Task {

	protected ITaskCallback m_Callback;

	public boolean isRunning = false;

	private String taskId = "";
	
	public String stringData = "";

	public SimpleAbstractTask() {
		logTask();
	}

	public SimpleAbstractTask(String id) {
		logTask();
		taskId = id;
	}

	public SimpleAbstractTask(final ITaskCallback callback) {
		m_Callback = callback;
	}

	public void logTask() {

	}

	@Override
	public void beginTask(ITaskCallback callback) {
			synchronized (this) {
				callback.onTaskStart(this);
				if (!isExecuting()) {
					isRunning = true;
					execute();
				}
			}
		
	}

	@Override
	public void beginAtomicTask(ITaskCallback callback) {
		this.m_Callback = callback;
		if (!isExecuting()) {
			m_Callback.onAtomicTaskStart(this);
			execute();
		}
	}

	@Override
	public boolean isExecuting() {
		return isRunning;
	}

	@Override
	public void stopTask() {
		/*
		 * Calling m_Callback.onTaskFinished(this) first doesn't call onFinished()
		 * after for some reason. Referencing/blocking issue in callback?
		 */
		
		onFinished();
		m_Callback.onTaskFinished(this);
//		onFinished();
	}

	@Override
	public void stopAtomicTask() {
		m_Callback.onTaskFinished(this);
		onFinished();
	}

	@Override
	public void setTaskId(String id) {
		this.taskId = id;
	}

	@Override
	public String getTaskId() {
		return this.taskId;
	}
	
	@Override
	public String getStringData() {
		return stringData;
	}
	
	@Override
	public void setStringData(final String data) {
		stringData = data;
	}

	public ITaskCallback getTaskCallback() {
		return m_Callback;
	}
}
