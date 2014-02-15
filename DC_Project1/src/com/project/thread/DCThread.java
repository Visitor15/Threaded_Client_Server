package com.project.thread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.project.framework.Task;
import com.project.tasks.ITaskCallback;

public class DCThread<T extends Task> extends Thread implements IDCThread,
		ITaskCallback {

	public enum THREAD_STATE {
		STOPPED, IDLE, PAUSED, RUNNING, FINISHED, FREE
	}

	public static final int THREAD_IDLE_LENGTH = 500;

	private THREAD_STATE m_ThreadState;

	private boolean atomicOperationInProgress;

	private ArrayList<Task> taskList = new ArrayList<Task>();

	private String threadId;

	private IThreadPoolCallback callback;

	public DCThread() {
		this.init();
	}

	public DCThread(String id, IThreadPoolCallback callback) {
		System.out.println("Created thread: " + id);
		this.callback = callback;
		this.threadId = id;
		this.init();
	}

	public DCThread(T task) {
		this.taskList.add(task);
	}
	
	public DCThread(String id, T task) {
		this.threadId = id;
		this.taskList.add(task);
	}

	public DCThread(List<T> taskList) {
		this.taskList.addAll(taskList);
	}

	private void init() {
		taskList = new ArrayList<Task>();
		this.setThreadState(THREAD_STATE.FREE);
		this.start();
	}

	@Override
	public void addBatchTasks(List<Task> tasks) {
		for (int i = 0; i < taskList.size(); i++) {
			this.taskList.add(tasks.get(i));
		}
	}

	@Override
	public void addTask(Task task) {
		this.taskList.add(task);
	}

	@Override
	public void startThread(boolean isAtomicOperation) {
		this.atomicOperationInProgress = isAtomicOperation;

		if (atomicOperationInProgress) {
//			System.out.println("Starting ATOMIC task(s)");
		} else {
//			System.out.println("Starting task(s)");
		}

		this.start();
	}

	@Override
	public void run() {
		setThreadState(THREAD_STATE.RUNNING);

		if (taskList.size() > 0) {
			executeTasks();
		}

		idleThread();
	}

	@Override
	public void executeTasks() {
		
		System.out.println("Thread: " + threadId + " is executing a task");
		
		setThreadState(THREAD_STATE.RUNNING);
		for (int i = 0; i < taskList.size(); i++) {
			Task task = taskList.remove(i);
//			System.out.println("EXECUTING ON THREAD: " + threadId);

			task.beginTask(this);
			/*
			 * If tasks are to be executed in a specific order, we do nothing
			 * until this task has finished.
			 */

			// do {
			// task.beginAtomicTask(this);
			// } while (atomicOperationInProgress);

			onFinished();
		}
	}

	@Override
	public void onProgressUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinished() {
//		System.out.println("Thread finished: " + threadId);
		this.setThreadState(THREAD_STATE.FINISHED);
		this.callback.onThreadFinished(this);
		this.idleThread();
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

	@Override
	public void idleThread() {
		this.setThreadState(THREAD_STATE.IDLE);
		do {
			try {
				if (callback != null) {
					// System.out.println("Thread: " + threadId +
					// " polling for tasks.");
//					Thread.sleep(500);

					
					
					int prev_Size = taskList.size();
					if (taskList.size() == 0) {
						this.addTask(callback.getNextTask());
						this.executeTasks();
					}
				}

//				Thread.sleep(THREAD_IDLE_LENGTH);
			} catch (NullPointerException e) {
//				System.out.println("No tasks available");
			}
		} while (getThreadState() == THREAD_STATE.IDLE);
	}

	@Override
	public void setThreadState(THREAD_STATE state) {
		this.m_ThreadState = state;
	}

	@Override
	public THREAD_STATE getThreadState() {
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

		taskList.remove(task);

		if (taskList.size() == 0) {
			this.setThreadState(THREAD_STATE.FINISHED);
		} else {
			this.executeTasks();
		}
	}

	@Override
	public void onTaskProgress(Task task) {
		// TODO Auto-generated method stub

	}

}