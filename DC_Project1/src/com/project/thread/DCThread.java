package com.project.thread;

import java.util.ArrayList;
import java.util.List;

import com.project.framework.Task;
import com.project.tasks.ITaskCallback;
import com.project.tasks.ThreadHelper;

public class DCThread<T extends Task> extends Thread implements IDCThread,
		ITaskCallback {

	/*
	 * Generated ID for Serializa
	 */
	private static final long serialVersionUID = -7555148602849298330L;

	public enum THREAD_STATE {
		STOPPED, IDLE, PAUSED, RUNNING, FINISHED, FREE, READY_FOR_NEXT_TASK
	}

	public static final int THREAD_IDLE_LENGTH = 500;

	private volatile THREAD_STATE m_ThreadState;

	private boolean atomicOperationInProgress;

	private ArrayList<Task> taskList;

	private String threadId;

	private IThreadPoolCallback callback;

	private ITaskCallback taskCallback;

	public DCThread() {
		this.init();
	}

	public DCThread(String id, IThreadPoolCallback callback) {
		// System.out.println("Created thread: " + id);
		this.callback = callback;
		this.threadId = id;
		this.init();
	}

	public DCThread(T task) {
		this.init();
		this.taskList.add(task);
	}

	public DCThread(String id, T task) {
		this.init();
		this.threadId = id;
		this.taskList.add(task);
	}

	public DCThread(List<T> taskList, IThreadPoolCallback callback) {
		this.init();
		this.callback = callback;
		this.taskList.addAll(taskList);
	}

	private void init() {
		taskList = new ArrayList<Task>();
		this.setThreadState(THREAD_STATE.FREE);
	}

	public String getThreadId() {
		return threadId;
	}

	@Override
	public void addBatchTasks(List<Task> tasks) {
		for (int i = 0; i < taskList.size(); i++) {
			this.taskList.add(tasks.get(i));
		}
	}

	@Override
	public void addTask(Task task) {
		// System.out.println("Adding task: " + task.getTaskId() +
		// " to thread: "
		// + getThreadId());

		this.taskList.add(task);

	}

	@Override
	public void startThread(boolean isAtomicOperation) {
		setThreadState(THREAD_STATE.READY_FOR_NEXT_TASK);
		this.atomicOperationInProgress = isAtomicOperation;

		if (atomicOperationInProgress) {
			// System.out.println("Starting ATOMIC task(s)");
		} else {
			// System.out.println("Starting task(s)");
		}

		this.start();
	}

	@Override
	public void run() {
		setThreadState(THREAD_STATE.RUNNING);

		if (taskList.size() > 0) {
			executeCurrentTasks();
		}

		// idleThread();
	}

	@Override
	public void executeCurrentTasks() {

		// System.out.println("Thread: " + threadId + " is executing a task");

		setThreadState(THREAD_STATE.RUNNING);
		// System.out.println("Task list size: " + taskList.size());
		for (int i = 0; i < taskList.size(); i++) {
			Task task = taskList.remove(i);
			// System.out.println("Executing task " + task.getTaskId()
			// + " on thread " + getThreadId());

			taskCallback = task.getTaskCallback();

			if (taskCallback != null) {
				taskCallback.onTaskStart(task);
			}
			task.beginTask(this);

			do {
				if (getThreadState() == THREAD_STATE.FINISHED) {
					stopThread();
				}
			} while (getThreadState() != THREAD_STATE.READY_FOR_NEXT_TASK
					|| getThreadState() != THREAD_STATE.FINISHED);
		}

	}

	@Override
	public void onProgressUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinished() {
		System.out.println("Thread " + getThreadId() + " finished: ");
		callback.onThreadFinished(this);
	}

	@Override
	public synchronized boolean isBusy() {
		return (this.getThreadState() == THREAD_STATE.RUNNING);
	}

	@Override
	public void setCallback(IThreadPoolCallback callback) {
		this.callback = callback;
	}

	@Override
	public void stopThread() {
		this.onFinished();
	}

	// @Override
	// public void idleThread() {
	// setThreadState(THREAD_STATE.FREE);
	// do {
	//
	// // System.out.println("Task list size (Thread: " + getThreadId() +
	// // "): " + taskList.size());
	// synchronized (this) {
	// if (taskList.size() > 0) {
	// System.out.println("Thread " + getThreadId()
	// + " Found tasks to execute.");
	// executeCurrentTasks();
	//
	// }
	// }
	//
	// System.out.println("Thread " + getThreadId() + " sleeping.");
	// ThreadHelper.sleepThread(3000);
	//
	// } while (getThreadState() == THREAD_STATE.FREE);
	// }

	@Override
	public void setThreadState(THREAD_STATE state) {
		this.m_ThreadState = state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.project.thread.IDCThread#getThreadState() This can be called
	 * from a different thread.
	 */
	@Override
	public synchronized THREAD_STATE getThreadState() {
		return this.m_ThreadState;
	}

	@Override
	public void onTaskStart(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAtomicTaskStart(Task task) {
		this.atomicOperationInProgress = true;
	}

	@Override
	public void onTaskFinished(Task task) {

		System.out.println(task.getTaskId() + " on Task Finished HIT");
		task.setTaskCallback(taskCallback);
		if (taskCallback != null) {
			taskCallback.onTaskFinished(task);
		}

		setThreadState(THREAD_STATE.FINISHED);
		if (taskList.size() == 0) {
			onFinished();
		} else {
			setThreadState(THREAD_STATE.READY_FOR_NEXT_TASK);
			// this.executeTasks();
		}
	}

	@Override
	public void onTaskProgress(Task task) {
		// TODO Auto-generated method stub

	}

}