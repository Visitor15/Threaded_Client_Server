package com.project.tasks;

public class ThreadHelper {

	public ThreadHelper() {
		
	}
	
	public synchronized static void sleepThread(final int sleepLength) {
		try {
			Thread.sleep(sleepLength);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
