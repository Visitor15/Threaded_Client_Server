package com.project.tasks;

import com.project.framework.Task;

public class SendNetworkFileTask extends SimpleAbstractTask {
	
	public SendNetworkFileTask() {
		
	}

	@Override
	public void executeTask() {
		
		System.out.println("SendNetworkFileTask STARTING!");
		int count = 0;
		do {
			
			System.out.println("Hello " + ++count);
			
			ThreadHelper.sleepThread(1000);
			
			if(count == 25) {
				stopTask();
			}
		} while(isExecuting());
	}

	@Override
	public void onProgressUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinished() {
		System.out.println("SendNetworkFileTask ON FINISH!");
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

}
