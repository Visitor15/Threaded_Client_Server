package com.project.tasks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.project.framework.Task;
import com.project.thread.DCThreadPool;

public class TaskManager {

	public static final int POOL_SIZE = 1;

	private static TaskManager m_Instance;

	// private ArrayList<DCThreadPool<Task>> POOLS;

	private static DCThreadPool<Task> ThreadPool;

	private TaskManager() {

		initPools();
		m_Instance = this;
	}

	public synchronized static <T extends Task> boolean DoTask(final T task) {

		if (m_Instance == null) {
			new TaskManager();
		}

		return m_Instance.initTask(task);
	}

	public synchronized static <T extends Task> boolean DoTask(
			final List<T> taskList) {

		if (m_Instance == null) {
			new TaskManager();
		}

		return m_Instance.initTask(taskList);
	}

	public synchronized static <T extends Task> boolean DoTaskOnCurrentThread(
			final T task, final ITaskCallback callback) {
		if (m_Instance == null) {
			new TaskManager();
		}

		return m_Instance.initTaskOnCurrentThread(task, callback);
	}

	public synchronized static <T extends Task> boolean DoPersistentTask(
			final T task, final ITaskCallback callback) {
		if (m_Instance == null) {
			new TaskManager();
		}

		return m_Instance.initPersistentTask(task, callback);
	}

	private void initPools() {
		ThreadPool = new DCThreadPool<Task>();
	}

	/*
	 * Synchronized. Called internally, but could potentially be called from
	 * multiple threads.
	 */
	private <T extends Task> boolean initTask(final T task) {
		System.out.println("Initing task: " + task.getTaskId());

		return ThreadPool.doTask(task);
	}

	private boolean initPersistentTask(final Task task,
			final ITaskCallback callback) {
		System.out.println("Initing persistent task: " + task.getTaskId());

		return ThreadPool.doTaskPersistent(task);
	}

	private <T extends Task> boolean initTaskOnCurrentThread(final T task,
			final ITaskCallback callback) {
		task.beginTask(callback);

		return true;
	}

	/*
	 * Synchronized. Called internally, but could potentially be called from
	 * multiple threads.
	 */
	private synchronized <T extends Task> boolean initTask(
			final List<T> taskList) {

		// for (Iterator<DCThreadPool<Task>> it = POOLS.iterator();
		// it.hasNext();) {
		// DCThreadPool<Task> pool = it.next();
		//
		// // if(pool.doTasks(taskList)) {
		// // return true;
		// // }
		//
		//
		// }

		return false;
	}
}
