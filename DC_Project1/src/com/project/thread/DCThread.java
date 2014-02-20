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

	public DCThread(List<T> taskList, IThreadPoolCallback callback) {
		this.callback = callback;
		this.taskList.addAll(taskList);
	}

	private void init() {
		taskList = new ArrayList<Task>();
		this.setThreadState(THREAD_STATE.FREE);
		this.start();
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
		this.taskList.add(task);
	}

	@Override
	public void startThread(boolean isAtomicOperation) {
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
			executeTasks();
		}

		idleThread();
	}

	@Override
	public void executeTasks() {

		synchronized (this) {
			System.out.println("Thread: " + threadId + " is executing a task");

			setThreadState(THREAD_STATE.RUNNING);
			for (int i = 0; i < taskList.size(); i++) {
				Task task = taskList.remove(i);
				System.out.println("Executing task " + task.getTaskId()
						+ " on thread " + getThreadId());

				task.beginTask(this);

				do {
					if (getThreadState() == THREAD_STATE.FINISHED) {
						stopThread();
					}
				} while (getThreadState() != THREAD_STATE.READY_FOR_NEXT_TASK);

				/*
				 * If tasks are to be executed in a specific order, we do
				 * nothing until this task has finished.
				 */

				// do {
				// task.beginAtomicTask(this);
				// } while (atomicOperationInProgress);
			}

		}
	}

	@Override
	public void onProgressUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinished() {
		System.out.println("Thread " + getThreadId() + " finished: ");
		setThreadState(THREAD_STATE.FREE);
		callback.onThreadFinished(this);
		idleThread();
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
		this.setThreadState(THREAD_STATE.FREE);
		do {
			
			if(taskList.size() > 0) {
				System.out.println("Thread " + getThreadId() + " Found tasks to execute.");
				executeTasks();
			}
			
			System.out.println("Thread " + getThreadId() + " sleeping.");
			ThreadHelper.sleepThread(3000);

		} while (getThreadState() == THREAD_STATE.FREE);
	}

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

		System.out.println(this.getClass().getSimpleName()
				+ " on Task Finished HIT");

		taskList.remove(task);

		if (taskList.size() == 0) {
			setThreadState(THREAD_STATE.FINISHED);
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