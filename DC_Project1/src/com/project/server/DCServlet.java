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
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5648352445012133175L;;

	protected SERVLET_TYPE TYPE;

	protected ServerSocket m_ServerSocket;

	protected Socket m_RecievingSocket;

	private IServletCallback m_Callback;

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
		}
	}

	DCServlet(final String serverId, final SERVLET_TYPE servletType,
			final boolean startServlet, final IServletCallback callback) {

		setTaskId(serverId);
		this.TYPE = servletType;
		this.m_Callback = callback;

		if (startServlet) {
			startServlet();
		}
	}

	@Override
	public void checkResponses() {
		respondToRequest();
	}

	public synchronized IServletCallback getCallback() {
		return m_Callback;
	}

	// @Override
	// public void beginTask(ITaskCallback callback) {
	//
	// synchronized (this) {
	//
	// if (!isExecuting()) {
	// // startServlet();
	// isRunning = true;
	// execute();
	// }
	// }
	// }

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

	private void initBaseServlet() {
		registerUnknownServlet(false);
	}

	@Override
	public boolean isExecuting() {
		return isRunning;
	}

	@Override
	public void onFinished() {
		System.out.println(this.getClass().getSimpleName() + " finished");
	}

	@Override
	public boolean registerServlet(final boolean autoStart) {

		if (TYPE == null) {
			System.out.println("ERROR: NO TYPE FOR SERVLET");

			return false;
		}

		// if (autoStart) {
		// return startServlet();
		// }

		return true;
	}

	private void registerUnknownServlet(final boolean autoStart) {
		TYPE = SERVLET_TYPE.UNKOWN;
		registerServlet(autoStart);
	}

	@Override
	public boolean startServlet() {
		return ServletManager.REGISTER_SERVLET(this,
				((ITaskCallback) this.m_Callback));
	}

	@Override
	public boolean stopServlet() {
		if (getServerSocket() != null) {
			try {
				getServerSocket().close();
			} catch (IOException e) {
				e.printStackTrace();

				return false;
			}
		}
		stopTask();

		return true;
	}

}
