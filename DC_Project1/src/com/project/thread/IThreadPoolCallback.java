package com.project.thread;

import com.project.framework.Task;

public interface IThreadPoolCallback {

	public void onThreadFinished(IDCThread thread);
	
	public Task getNextTask();
	
	
}
