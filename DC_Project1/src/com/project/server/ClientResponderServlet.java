package com.project.server;

import com.project.framework.Task;

public class ClientResponderServlet extends DCServlet {

	public ClientResponderServlet(final boolean autoStart,
			final IServletCallback callback) {
		super(SERVLET_TYPE.CLIENT_RESPONDER_SERVLET, autoStart, callback);

		init();
	}

	private void init() {

	}

	@Override
	public void respondToRequest() {
		System.out.println("Client Responder responding.");
	}

	@Override
	public void receiveRequest() {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendResponse() {
		// TODO Auto-generated method stub

	}

	@Override
	public void executeTask() {
		do {
			for (int i = 0; i < 15; i++) {
				System.out.println("Client Responder checking in.");

//				try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}

//			super.stopServlet();

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
		getCallback().onFinishServlet(this);
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
