package com.project.tasks;

import java.io.Serializable;

import com.project.framework.Task;

public interface ITaskCallback extends Serializable {
	
	public void onTaskStart(final Task task);
	
	public void onAtomicTaskStart(final Task task);
	
	public void onTaskProgress(final Task task);
	
	public void onTaskFinished(final Task task);
	
}
