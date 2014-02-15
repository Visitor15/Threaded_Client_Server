package com.project.tasks;

import com.project.framework.Task;

public abstract class SimpleTask implements Task {

	protected ITaskCallback m_Callback;

	public boolean isRunning = false;

	private String taskId = "";

	public SimpleTask() {
		logTask();
	}

	public SimpleTask(String id) {
		logTask();
		taskId = id;
	}

	public SimpleTask(final ITaskCallback callback) {
		this.m_Callback = callback;
	}

	public void logTask() {

	}

	@Override
	public void beginTask(ITaskCallback callback) {

//			System.out.println("Beginning task");
//			this.m_Callback = callback;
//			// if (!isExecuting()) {
//			m_Callback.onTaskStart(this);
//			execute();
//			m_Callback.onTaskFinished(this);
//			// }
//
//			System.out.println("End task");
			
			synchronized (this) {
				callback.onTaskStart(this);
				if (!isExecuting()) {
					// startServlet();
					isRunning = true;
					execute();
				}
				callback.onTaskFinished(this);
			}
		
	}

	@Override
	public void beginAtomicTask(ITaskCallback callback) {
		this.m_Callback = callback;
		if (!isExecuting()) {
			m_Callback.onAtomicTaskStart(this);
			execute();
			m_Callback.onTaskFinished(this);
		}
	}

	@Override
	public boolean isExecuting() {
		return isRunning;
	}

	@Override
	public void stopTask() {
		m_Callback.onTaskFinished(this);
		onFinished();
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

	public ITaskCallback getTaskCallback() {
		return m_Callback;
	}
}
