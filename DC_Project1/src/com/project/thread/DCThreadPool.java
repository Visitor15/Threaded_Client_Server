package com.project.thread;

import java.util.ArrayList;
import java.util.List;

import com.project.framework.Task;
import com.project.tasks.SimpleAbstractTask;
import com.project.thread.DCThread.THREAD_STATE;

public class DCThreadPool<T extends Task> implements IThreadPoolCallback {

	public static final int POOL_SIZE = 4;

	public static final long DEF_WAIT_TIME = 250;

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

		this.forceNewTaskThread(new SimpleAbstractTask() {

			@Override
			public void execute() {
				do {
//					System.out.println("Attempting to execute next task.");
					executeNextTask();
					
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
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
						
						System.out.println("Adding task from 'executeNextTask'");
						
						thread.addTask(getNextTask());
						thread.startThread(false);
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
		// IDCThread<T> thread = (IDCThread<T>) getFreeThread();
		//
		// if (thread != null) {
		// thread.addTask(task);
		// thread.startThread(false);
		//
		// return true;
		// } else {
		// m_TaskQueue.add(task);
		// }

		m_TaskQueue.add(task);

		return true;
	}

	/* Thread-safe instance */
	public synchronized boolean doTask(final T task) {

		// if(task instanceof SimplePersistentTask) {
		// return doTaskPersistent(task);
		// }

		// IDCThread<T> thread = getFreeThread();
		//
		// if (thread != null) {
		// thread.setCallback(this);
		// thread.addTask(task);
		// thread.startThread(false);
		//
		// return true;
		// } else {
		// m_TaskQueue.add(task);
		// }

		System.out.println("Adding task");

		m_TaskQueue.add(task);

		return true;
	}

	public boolean doTaskPersistent(T persistentTask) {

		// IDCThread<T> thread = getFreeThread();
		//
		// if (thread != null) {
		// thread.setCallback(this);
		// thread.addTask(persistentTask);
		// thread.startThread(false);
		//
		// return true;
		// } else {
		// m_TaskQueue.add(persistentTask);
		// }

		m_TaskQueue.add(persistentTask);

		return true;
	}

	/* This is the heart */
	public boolean doTasks(final List<T> taskList) {

		// IDCThread<T> thread = getFreeThread();

		// if (thread != null) {
		// thread.addBatchTasks(taskList);
		// thread.startThread(false);
		//
		// return true;
		// } else {
		//
		// }

		m_BatchedTaskQueue.add(taskList);

		return true;
	}

	@Override
	public synchronized T getNextTask() {
		if (this.m_TaskQueue.size() > 0) {

			System.out
					.println("Giving task: " + m_TaskQueue.get(0).getTaskId());

			return m_TaskQueue.remove(0);
		}

		throw new NullPointerException();
	}

	@Override
	public void onThreadFinished(IDCThread thread) {
		thread.setThreadState(THREAD_STATE.FREE);

//		try {
//			thread.addTask(getNextTask());
//			thread.executeTasks();
//		} catch (final NullPointerException e) {
//
//		}
	}

}
