package com.project.thread;

import com.project.framework.Task;

public interface IThreadPoolCallback {

	public Task getNextTask();

	public void onThreadFinished(DCThread thread);

}
