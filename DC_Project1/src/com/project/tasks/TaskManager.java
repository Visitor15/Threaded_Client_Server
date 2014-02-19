package com.project.tasks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.project.framework.Task;
import com.project.thread.DCThreadPool;

public class TaskManager {
	
	public static final int POOL_SIZE = 2;
	
	private static TaskManager m_Instance;

	private ArrayList<DCThreadPool<Task>> POOLS;
	
	private TaskManager() {
		
		initPools();
		m_Instance = this;
	}
	
	public synchronized static <T extends Task> boolean DO_TASK(final T task) {
		
		if(m_Instance == null) {
			new TaskManager();
		}
		
		return m_Instance.initTask(task);
	}
	
	public synchronized static <T extends Task> boolean DO_TASK(final List<T> taskList) {
		
		if(m_Instance == null) {
			new TaskManager();
		}
		
		return m_Instance.initTask(taskList);
	}
	
	public synchronized static <T extends Task> boolean DoTaskOnCurrentThread(final T task, final ITaskCallback callback) {
		if(m_Instance == null) {
			new TaskManager();
		}
		
		return m_Instance.initTaskOnCurrentThread(task, callback);
	}
	
	private void initPools() {
		POOLS = new ArrayList<DCThreadPool<Task>>();
		POOLS.add(new DCThreadPool<Task>());
//		for(int i = 0; i < POOL_SIZE; i++) {
//			POOLS.add(new DCThreadPool<Task>());
//		}
	}
	
	/* Synchronized.
	 * Called internally, but could potentially be called from multiple threads. */
	private <T extends Task> boolean initTask(final T task) {
		System.out.println("Initing task");
		
		POOLS.get(0).doTask(task);
		
		return true;
		
//		for(int i = 0; i < POOLS.size(); i++) {
//			DCThreadPool<Task> pool = POOLS.get(i);
//			
//			if(pool.doTask(task)) {
//				System.out.println("Doing task");
//				return true;
//			}
//		}
//		
//		return false;
	}
	
	private <T extends Task> boolean initTaskOnCurrentThread(final T task, final ITaskCallback callback) {
		task.beginTask(callback);
		
		return true;
	}
	
	/* Synchronized.
	 * Called internally, but could potentially be called from multiple threads. */
	private synchronized <T extends Task> boolean initTask(final List<T> taskList) {
		
		for (Iterator<DCThreadPool<Task>> it = POOLS.iterator(); it.hasNext();) {
			DCThreadPool<Task> pool = it.next();
			
//			if(pool.doTasks(taskList)) {
//				return true;
//			}
			
			
		}
		
		return false;
	}
}
