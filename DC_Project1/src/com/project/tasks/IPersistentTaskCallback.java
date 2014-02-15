package com.project.tasks;

public interface IPersistentTaskCallback extends ITaskCallback {
	
	public void onTaskTick();

	public void onTaskFinished(final SimplePersistentTask task);
}
