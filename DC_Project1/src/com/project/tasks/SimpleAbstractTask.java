package com.project.tasks;

import com.project.framework.Task;

public abstract class SimpleAbstractTask implements Task {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2163720329964850383L;

	protected ITaskCallback m_Callback;

	private ITaskCallback m_ThreadCallback;

	public boolean isRunning = false;

	private String taskId = "";

	public String stringData = "";

	public SimpleAbstractTask() {
		logTask();
	}

	public SimpleAbstractTask(final ITaskCallback callback) {
		m_Callback = callback;
	}

	public SimpleAbstractTask(final ITaskCallback callback, final String id) {
		m_Callback = callback;
		taskId = id;
	}

	public SimpleAbstractTask(String id) {
		logTask();
		taskId = id;
	}

	@Override
	public void beginAtomicTask(ITaskCallback callback) {
		this.m_Callback = callback;
		if (!isExecuting()) {
			m_Callback.onAtomicTaskStart(this);
			executeTask();
		}
	}

	@Override
	public void beginTask(ITaskCallback threadCallback) {
		// synchronized (this) {
		m_ThreadCallback = threadCallback;

		// if (m_Callback != null) {
		// m_Callback.onTaskStart(this);
		// }

		if (m_ThreadCallback != null) {
			m_ThreadCallback.onTaskStart(this);
		}
		if (!isExecuting()) {
			isRunning = true;
			executeTask();
		}
		// }
	}

	@Override
	public String getStringData() {
		return stringData;
	}

	@Override
	public ITaskCallback getTaskCallback() {
		return m_Callback;
	}

	@Override
	public String getTaskId() {
		return taskId;
	}

	@Override
	public boolean isExecuting() {
		return isRunning;
	}

	@Override
	public void logTask() {

	}

	@Override
	public void setStringData(final String data) {
		stringData = data;
	}

	@Override
	public void setTaskCallback(final ITaskCallback callback) {
		this.m_Callback = callback;
	}

	@Override
	public void setTaskId(String id) {
		taskId = id;
	}

	@Override
	public void stopAtomicTask() {
		onFinished();
		m_ThreadCallback.onTaskFinished(this);
	}

	@Override
	public void stopTask() {
		/*
		 * Calling m_Callback.onTaskFinished(this) first doesn't call
		 * onFinished() after for some reason. Referencing/blocking issue in
		 * callback?
		 */
		isRunning = false;
		onFinished();

		if (m_ThreadCallback != null) {
			m_ThreadCallback.onTaskFinished(this);
		}
		// if (m_ThreadCallback == null && m_Callback != null) {
		// m_Callback.onTaskFinished(this);
		// }

		// onFinished();
	}
}
