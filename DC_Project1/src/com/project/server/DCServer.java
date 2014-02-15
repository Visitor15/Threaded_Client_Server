package com.project.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import com.project.io.SynchedInOut;
import com.project.server.DCServlet.SERVLET_TYPE;
import com.project.server.router.Client;
import com.project.tasks.FindDefaultGatewayTask;
import com.project.tasks.SimpleTask;
import com.project.tasks.TaskManager;

public class DCServer implements IServletCallback {

	private static DCServer mInstance;

	private static HashMap<SERVLET_TYPE, DCServlet> m_ServletMap;

	private static final String HOSTNAME = "com.dcproject.def_hostname";

	private static final int DEF_PORT = 1337;

	private DCServer(final boolean autoStart) {
		m_ServletMap = new HashMap<SERVLET_TYPE, DCServlet>();

		if (autoStart) {
			start();
		}

		mInstance = this;
	}

	public static synchronized DCServer GET_INSTANCE() {
		if (mInstance == null) {
			new DCServer(false);
		}

		return mInstance;
	}

	public void start() {
		tryFindDefaultGateway();
		registerDefaultServlets();

//		testCode();

		beginServing();
	}

	public void idle() {

	}

	public void stop() {

	}

	public void registerDefaultServlets() {

//		m_ServletMap.put(SERVLET_TYPE.REGISTRATION_SERVLET,
//				new ClientRegistrationServlet(true, this));
		m_ServletMap.put(SERVLET_TYPE.CLIENT_RESPONDER_SERVLET, new ServerIdentifierServlet(true, this));
		m_ServletMap.put(SERVLET_TYPE.SERVER_DISCOVERY_SERVLET, new ServerDiscoveryServlet(true, this));
		// m_ServletMap.put(SERVLET_TYPE.CLIENT_RESPONDER_SERVLET,
		// new ClientResponderServlet(true, this));

		// m_ServletMap.get(SERVLET_TYPE.REGISTRATION_SERVLET).startServlet();
	}

	private void beginServing() {
		if (m_ServletMap.size() > 0) {
			do {
				// System.out.println("=========================");
				// System.out.println("SERVER PULSE");
				// System.out.println("=========================");
				for (int i = 0; i < SERVLET_TYPE.values().length; i++) {
					SERVLET_TYPE servletType = SERVLET_TYPE.values()[i];

					/*
					 * In the event we want to do something before we have the
					 * servlet respond.
					 */
					switch (servletType) {
					case CLIENT_RESPONDER_SERVLET:
						break;
					case REGISTRATION_SERVLET:
						break;
					case UNKOWN:
						break;
					default:
						break;
					}

					if (m_ServletMap.containsKey(servletType)) {

						// System.out.println("=========================");
						// System.out.println("contains key");
						// System.out.println("=========================");

						/* Servlet call */
//						m_ServletMap.get(servletType).checkResponses();

						// if (!m_ServletMap.get(servletType).isExecuting()) {
						//
						// System.out.println("=========================");
						// System.out.println("Removing servlet");
						// System.out.println("=========================");
						//
						// m_ServletMap.remove(servletType);
						// }
					}
				}

				/* Give some time for servlets to do their thing. */
				// try {
				// Thread.sleep(1000);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }

			} while (m_ServletMap.size() > 0);
		}
	}

	public void checkResponses(DCServlet servlet) {
		System.out.println("Checking responses");
		switch (servlet.TYPE) {
		case CLIENT_RESPONDER_SERVLET:
			break;
		case REGISTRATION_SERVLET:
			break;
		case UNKOWN:
			break;
		default:
			break;
		}

		if (m_ServletMap.containsKey(servlet.TYPE)) {
			m_ServletMap.get(servlet.TYPE).respondToRequest();
		}
	}
	
	private void tryFindDefaultGateway() {
		TaskManager.DO_TASK(new FindDefaultGatewayTask());
	}

	@Override
	public <T extends IDCServlet> void onRegisterServlet(T servlet) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends IDCServlet> void onStartServlet(T servlet) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends IDCServlet> void onServletEvent(T servlet) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T extends IDCServlet> void onFinishServlet(T servlet) {
		m_ServletMap.remove(servlet);
	}

	public static String GET_HOSTNAME() {
		return HOSTNAME;
	}

	public static int GET_DEF_PORT() {
		return DEF_PORT;
	}

	public void testCode() {
		int count = 0;
		boolean success;
		do {
			count++;
			success = TaskManager.DO_TASK(new SimpleTask("MESSAGE TASK") {

				@Override
				public synchronized void execute() {

					for (int i = 0; i < 1; i++) {
						try {

							// Thread.sleep(5000);

							System.out.println("BEGIN EXECUTION");
							
							String mMessage = "";
							
							
							mMessage = SynchedInOut.getInstance().postMessageForUserInput("Message " + i + ": ");

							Socket sock = new Socket(InetAddress.getLocalHost().getHostName(), 1337);
							
							System.out.println("NEXT STEP");
							
							System.out.println("Message " + i + ": ");

							Client client = new Client(sock.getInetAddress()
									.getHostAddress(), sock.getLocalPort());

							client.addStringMessage(mMessage);

							PrintWriter out = new PrintWriter(sock
									.getOutputStream(), true);
							BufferedReader in = new BufferedReader(
									new InputStreamReader(sock.getInputStream()));

							out.println(mMessage + " : " + i);

							String fromServer = "null";
							while ((fromServer = in.readLine()) != null) {
								switch (Integer.valueOf(fromServer)) {
								case 200: {
									System.out.println("I registered!");
									break;
								}
								case 401: {
									System.out
											.println("Authentication exception");
									break;
								}
								default: {
									System.out.println("Unknown Exception");
									break;
								}
								}
							}

							out.flush();

							in.close();
							out.close();
							sock.close();

							// Thread.sleep(3000);

						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}

				@Override
				public void onProgressUpdate() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onFinished() {
					// TODO Auto-generated method stub

				}

			});

		} while (count < 1);
	}
}
