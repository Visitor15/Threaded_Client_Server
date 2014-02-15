package com.project.io;

import java.util.Scanner;

public class SynchedInOut {

	private static SynchedInOut m_Instance;
	
	private boolean hasScanner = false;
	
	private Scanner input;
	
	private SynchedInOut() {
		input = new Scanner(System.in);
		m_Instance = this;
	}
	
	public synchronized static SynchedInOut getInstance() {
		if (m_Instance == null) {
			new SynchedInOut();
		}
		
		return m_Instance;
	}
	
	public String postMessageForUserInput(final String message) {
		
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while(hasScanner);
		
		hasScanner = true;
		
		System.out.print(message);
		String userInput = input.nextLine();
		
		hasScanner = false;
		
		return userInput;
	}
	
	public void postMessageNewLine(final String message) {
		
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while(hasScanner);
		
		hasScanner = true;
		System.out.println(message);
		hasScanner = false;
	}
}
