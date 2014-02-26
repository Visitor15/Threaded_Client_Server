package com.project.tasks;

public abstract class SimplePersistentTask extends SimpleAbstractTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5144299437816862239L;

	public SimplePersistentTask() {
		logTask();
	};

	public SimplePersistentTask(final IPersistentTaskCallback callback) {
		super(callback);
	}

	@Override
	public void logTask() {

	}
}
