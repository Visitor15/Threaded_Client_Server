package com.project.tasks;

public abstract class SimplePersistentTask extends SimpleTask {

	public SimplePersistentTask() {
		logTask();
	};
	
	public SimplePersistentTask(final IPersistentTaskCallback callback) {
		this.m_Callback = callback;
	}
	
	public void logTask() {
		
	}
}
