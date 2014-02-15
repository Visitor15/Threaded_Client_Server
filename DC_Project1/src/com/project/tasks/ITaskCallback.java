package com.project.tasks;

import com.project.framework.Task;

public interface ITaskCallback {
	
	public void onTaskStart(final Task task);
	
	public void onAtomicTaskStart(final Task task);
	
	public void onTaskProgress(final Task task);
	
	public void onTaskFinished(final Task task);
	
}
