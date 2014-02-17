package com.project.tasks;

public abstract class SimplePersistentTask extends SimpleAbstractTask {

	public SimplePersistentTask() {
		logTask();
	};
	
	public SimplePersistentTask(final IPersistentTaskCallback callback) {
		super(callback);
	}
	
	public void logTask() {
		
	}
}
