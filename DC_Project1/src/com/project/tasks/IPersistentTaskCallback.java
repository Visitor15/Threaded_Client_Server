package com.project.tasks;

public interface IPersistentTaskCallback extends ITaskCallback {

	public void onTaskFinished(final SimplePersistentTask task);

	public void onTaskTick();
}
