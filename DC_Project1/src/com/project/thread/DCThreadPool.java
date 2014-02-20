package com.project.thread;

import java.util.ArrayList;
import java.util.List;

import com.project.framework.Task;
import com.project.tasks.ITaskCallback;
import com.project.tasks.SimpleAbstractTask;
import com.project.tasks.ThreadHelper;
import com.project.thread.DCThread.THREAD_STATE;

public class DCThreadPool<T extends Task> implements IThreadPoolCallback,
		ITaskCallback {

	public static final int POOL_SIZE = 4;

	public static final long DEF_WAIT_TIME = 250;

	private static final String MONITOR_TASK_ID = "Monitor Tasks... Task";

	private List<DCThread<T>> m_Threads;
	private List<DCThread<Task>> m_ForcedThreads;

	private List<T> m_TaskQueue;
	private List<List<T>> m_BatchedTaskQueue;

	public DCThreadPool() {

		init();
	}

	private void init() {
		m_Threads = new ArrayList<DCThread<T>>();
		m_ForcedThreads = new ArrayList<DCThread<Task>>();
		m_TaskQueue = new ArrayList<T>();
		m_BatchedTaskQueue = new ArrayList<List<T>>();

		/* Creating threads */
		for (int i = 0; i < POOL_SIZE; i++) {
			m_Threads.add(new DCThread<T>(Integer.toString(i), this));
		}

		startTaskMonitorThread();
		System.out.println("Finished starting threads");
	}

	private void startTaskMonitorThread() {

		this.forceNewTaskThread(new SimpleAbstractTask(this, MONITOR_TASK_ID) {

			@Override
			public void executeTask() {
				do {
					// System.out.println("Attempting to execute next task.");
					executeNextTask();

					ThreadHelper.sleepThread(3000);

				} while (this.isExecuting());
			}

			@Override
			public void onProgressUpdate() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub

			}

			@Override
			public byte[] toBytes() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Task fromBytes(byte[] byteArray) {
				// TODO Auto-generated method stub
				return null;
			}

		});

	}

	private synchronized void executeNextTask() {
		if (this.m_TaskQueue.size() > 0) {

			for (int i = 0; i < this.m_Threads.size(); i++) {
				DCThread thread = this.m_Threads.get(i);

				if (thread.getThreadState() == THREAD_STATE.FREE) {
					try {
						Task mTask = getNextTask();

						if (mTask == null) {
							System.out.println("No tasks in task queue.");
							return;
						}

						System.out.println("Giving task " + mTask.getTaskId()
								+ " to thread " + thread.getThreadId());

						thread.addTask(mTask);
						// thread.startThread(false);
					} catch (final NullPointerException e) {

					}
				}
			}

		}
	}

	private DCThread<T> getFreeThread() {

		System.out.println("Thread pool size: " + m_Threads.size());
		if (m_Threads.size() < 4) {
			DCThread<T> thread = new DCThread<T>();
			thread.setCallback(this);
			;
			m_Threads.add(thread);
			return thread;
		}

		// for (Iterator<DCThread<T>> it = m_Threads.iterator(); it.hasNext();)
		// {
		// DCThread<T> thread = it.next();
		// if (!thread.isBusy()) {
		// thread = new DCThread<T>();
		// System.out.println("Running from thread: " +
		// m_Threads.indexOf(it.next()));
		// return thread;
		// }
		// }

		return null;
	}

	private void forceNewTaskThread(Task task) {

		DCThread<Task> thread = new DCThread<Task>("Forced thread", task);
		thread.setCallback(this);
		thread.addTask(task);
		thread.startThread(false);
		m_ForcedThreads.add(thread);
	}

	/* Thread-safe instance */
	public synchronized boolean doTaskSynchronus(T task) {
		synchronized (this) {
			m_TaskQueue.add(task);
		}
		return true;
	}

	/* Thread-safe instance */
	public synchronized boolean doTask(final T task) {

		System.out.println("Adding task: " + task.getTaskId());

		synchronized (this) {
			m_TaskQueue.add(task);
		}

		return true;
	}

	public boolean doTaskPersistent(T persistentTask) {
		m_TaskQueue.add(persistentTask);

		return true;
	}

	/* This is the heart */
	public boolean doTasks(final List<T> taskList) {
		m_BatchedTaskQueue.add(taskList);

		return true;
	}

	@Override
	public synchronized T getNextTask() {
		if (this.m_TaskQueue.size() > 0) {
			return m_TaskQueue.remove(0);
		}

		throw new NullPointerException();
	}

	@Override
	public void onThreadFinished(IDCThread thread) {
		thread.setThreadState(THREAD_STATE.FREE);

		// try {
		// thread.addTask(getNextTask());
		// thread.executeTasks();
		// } catch (final NullPointerException e) {
		//
		// }
	}

	@Override
	public void onTaskStart(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAtomicTaskStart(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskProgress(Task task) {
		if (task.getTaskId().equals(MONITOR_TASK_ID)) {
			executeNextTask();
		}
	}

	@Override
	public void onTaskFinished(Task task) {
		// TODO Auto-generated method stub

	}

}
