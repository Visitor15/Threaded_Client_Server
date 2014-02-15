package com.project.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.project.tasks.ITaskCallback;
import com.project.tasks.SimplePersistentTask;

public abstract class DCServlet extends SimplePersistentTask implements
		IDCServlet {

	protected enum SERVLET_TYPE {
		REGISTRATION_SERVLET, CLIENT_RESPONDER_SERVLET, SERVER_DISCOVERY_SERVLET, SERVER_IDENTIFIER_SERVLET, UNKOWN
	};

	protected SERVLET_TYPE TYPE;

	protected ServerSocket m_ServerSocket;

	protected Socket m_RecievingSocket;

	private IServletCallback m_Callback;

	private boolean isRunning = false;

	DCServlet() {
		super();
		initBaseServlet();
	}

	DCServlet(final SERVLET_TYPE servletType, final boolean startServlet,
			final IServletCallback callback) {

		this.TYPE = servletType;
		this.m_Callback = callback;

		if (startServlet) {
			startServlet();
		} else {
			initBaseServlet();
		}
	}

	private void initBaseServlet() {
		registerUnknownServlet(false);
	}

	private void initAndStartBaseServlet() {
		registerUnknownServlet(true);
	}

	private void registerUnknownServlet(final boolean autoStart) {
		TYPE = SERVLET_TYPE.UNKOWN;
		registerServlet(autoStart);
	}

	@Override
	public void beginTask(ITaskCallback callback) {

		synchronized (this) {

			if (!isExecuting()) {
				// startServlet();
				isRunning = true;
				execute();
			}
		}
	}

	@Override
	public boolean isExecuting() {
		return isRunning;
	}

	@Override
	public boolean startServlet() {
		return ServletManager.REGISTER_SERVLET(this);
	}

	@Override
	public boolean stopServlet() {

		boolean canStop = !isRunning;

		if (!canStop) {

		}

		if (m_RecievingSocket != null) {
			try {
				m_RecievingSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return canStop;
	}

	@Override
	public boolean registerServlet(final boolean autoStart) {

		if (TYPE == null) {
			System.out.println("ERROR: NO TYPE FOR SERVLET");

			return false;
		}

		if (autoStart) {
			return startServlet();
		}

		return false;
	}

	@Override
	public void checkResponses() {
		respondToRequest();
	}

	public synchronized ServerSocket getServerSocket() {
		if (m_ServerSocket == null) {
			try {
				m_ServerSocket = new ServerSocket();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return m_ServerSocket;
	}

	public synchronized IServletCallback getCallback() {
		return m_Callback;
	}

}
