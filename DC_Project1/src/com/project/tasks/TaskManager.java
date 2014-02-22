package com.project.tasks;

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
	
	private void initPools() {
		ThreadPool = new DCThreadPool<Task>();
	}

	public synchronized static <T extends Task> boolean DoTask(final T task) {

		if (m_Instance == null) {
			new TaskManager();
		}

		return m_Instance.initTask(task);
	}

	public synchronized static <T extends Task> void DoTask(
			final List<T> taskList) {

		if (m_Instance == null) {
			new TaskManager();
		}

		m_Instance.initTask(taskList);
	}

	public synchronized static <T extends Task> void DoTaskOnCurrentThread(
			final T task, final ITaskCallback callback) {
		if (m_Instance == null) {
			new TaskManager();
		}

		m_Instance.initTaskOnCurrentThread(task, callback);
	}

	public synchronized static <T extends Task> boolean DoPersistentTask(
			final T task, final ITaskCallback callback) {
		if (m_Instance == null) {
			new TaskManager();
		}

		return m_Instance.initPersistentTask(task, callback);
	}
	
	private <T extends Task> boolean initTask(final T task) {
		return ThreadPool.doTask(task);
	}

	private boolean initPersistentTask(final Task task,
			final ITaskCallback callback) {
		return ThreadPool.doTaskPersistent(task);
	}

	private <T extends Task> void initTaskOnCurrentThread(final T task,
			final ITaskCallback callback) {
		task.beginTask(callback);
	}
	
	private <T extends Task> void initTask(
			final List<T> taskList) {
		
	}
}
