package com.project.tasks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.project.framework.Task;
import com.project.io.SynchedInOut;

public class PostMessageTask extends SimpleAbstractTask {
	
	/*
	 *	Generated ID for Serializable. 
	 */
	private static final long serialVersionUID = -85253068768354643L;

	public PostMessageTask() {
		setTaskId("PostMessageTask");
	}
	
	public void setMessage(final String message) {
		setStringData(message);
	}
	
	public String getMessage() {
		return getStringData();
	}

	@Override
	public void executeTask() {
		SynchedInOut.getInstance().postMessageNewLine("Message received: " + getStringData());
		stopTask();
	}

	@Override
	public void onProgressUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinished() {
		// TODO Auto-generated method stub
		System.out.println("PostMessageTask finishing");
	}

	@Override
	public byte[] toBytes() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutputStream out;
		
		try {
			out = new ObjectOutputStream(os);
			
//			int length = (getTaskId().getBytes().length + getStringData().getBytes().length);
			
			out.writeUTF(getTaskId());
			out.writeUTF(getStringData());
			out.flush();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		final byte[] res = os.toByteArray();

		return res;
	}

	@Override
	public Task fromBytes(byte[] byteArray) {
		PostMessageTask task = new PostMessageTask();
		ByteArrayInputStream is;
		ObjectInputStream in;
		
		String tmpTaskId;
		String tmpStringData;
		
		try {
			is = new ByteArrayInputStream(byteArray);
			in = new ObjectInputStream(is);

			tmpTaskId = in.readUTF();
			tmpStringData = in.readUTF();
			
			task.setTaskId(tmpTaskId);
			task.setStringData(tmpStringData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return task;
	}


	public static Task fromNewBytes(byte[] byteArray) {
		PostMessageTask task = new PostMessageTask();
		ByteArrayInputStream is;
		ObjectInputStream in;
		
		String tmpTaskId;
		String tmpStringData;
		
		try {
			is = new ByteArrayInputStream(byteArray);
			in = new ObjectInputStream(is);

			tmpTaskId = in.readUTF();
			tmpStringData = in.readUTF();
			
			task.setTaskId(tmpTaskId);
			task.setStringData(tmpStringData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return task;
	}
}
