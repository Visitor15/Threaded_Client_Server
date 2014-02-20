package com.project.tasks;

import com.project.framework.Task;
import com.project.io.SynchedInOut;

public class RegisterClientTask extends SimpleAbstractTask {

	private int id;

	public RegisterClientTask(int id) {
		super();
		this.id = id;
	}

	public RegisterClientTask(final ITaskCallback callback) {
		super(callback);
	}

	@Override
	public void executeTask() {
		do {
			for (int i = 0; i < 15; i++) {
				
				SynchedInOut.getInstance().postMessageNewLine("RegistrationTask checking in.");

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			stopTask();

		} while (isExecuting());

		onFinished();
	}

	@Override
	public void onProgressUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinished() {
		System.out.println("Client Responder shutting down.");
//		getTaskCallback().onTaskFinished(this);
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
