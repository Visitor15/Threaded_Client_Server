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
		// System.out.println("Finished starting threads");
	}

	private void startTaskMonitorThread() {

		this.forceNewTaskThread(new SimpleAbstractTask(DCThreadPool.this,
				MONITOR_TASK_ID) {

			@Override
			public void executeTask() {
				do {
					// System.out.println("Attempting to execute next task.");
					// synchronized (this) {
					// System.out.println("Attempting to execute next task.");
					synchronized (DCThreadPool.this) {
						executeNextTask();
					}
					// }

					ThreadHelper.sleepThread(500);

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

	public synchronized void executeNextTask() {
		// System.out.println("HIT");
		if (this.m_TaskQueue.size() > 0) {

			for (int i = 0; i < this.m_Threads.size(); i++) {
				DCThread thread = this.m_Threads.get(i);
				// System.out.println("Got thread: " + thread.getThreadId());
				if (thread.getThreadState() == THREAD_STATE.FREE) {
					// System.out.println("HIT3");
					try {
						Task mTask = getNextTask();

						if (mTask == null) {
							// System.out.println("No tasks in task queue.");
							return;
						}

						 System.out.println("Giving task "
						 + mTask.getTaskId() + " to thread "
						 + thread.getThreadId());

						thread.addTask(mTask);

						thread.startThread(false);
						// thread.startThread(false);
					} catch (final NullPointerException e) {

					}
				} else {
					// System.out.println("Bing Bong");
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

		DCThread<Task> thread = new DCThread<Task>(
				"Forced thread - Monitoring", task);
		thread.setCallback(this);
		// thread.addTask(task);
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

		// System.out.println("Adding task: " + task.getTaskId());

		m_TaskQueue.add(task);

		return true;
	}

	public boolean doTaskPersistent(T persistentTask) {
		DCThread<Task> thread = new DCThread<Task>("Forced thread - DCServer",
				persistentTask);
		thread.setCallback(this);
		// thread.addTask(persistentTask);
		thread.startThread(false);
		m_ForcedThreads.add(thread);

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
	public void onThreadFinished(DCThread thread) {
		thread.setThreadState(THREAD_STATE.FREE);

		// System.out.println("THREAD FINISHED: ");

		if (this.m_Threads.contains(thread)) {
			// System.out.println("THREAD REMOVED: ");
			m_Threads.remove(thread);
			thread.interrupt();
			thread = null;

			m_Threads.add(new DCThread<T>("New thread " + m_Threads.size(),
					this));
		}

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
