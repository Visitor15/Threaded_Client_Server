package com.project.thread;

import com.project.framework.Task;

public interface IThreadPoolCallback {

	public void onThreadFinished(DCThread thread);
	
	public Task getNextTask();
	
	
}
