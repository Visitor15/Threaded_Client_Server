package com.project.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import com.project.framework.Task;
import com.project.io.SynchedInOut;
import com.project.server.DCServlet.SERVLET_TYPE;
import com.project.server.router.Client;
import com.project.server.router.RoutingTable;
import com.project.tasks.FindDefaultGatewayTask;
import com.project.tasks.ITaskCallback;
import com.project.tasks.SimpleAbstractTask;
import com.project.tasks.TaskManager;

public class DCServer implements IServletCallback, ITaskCallback {

	private static volatile DCServer mInstance;

	private static volatile HashMap<SERVLET_TYPE, DCServlet> m_ServletMap;
	
	private static volatile String DEFAULT_GATEWAY = "NULL";

	private static volatile String HOSTNAME = "com.dcproject.def_hostname";

	private static volatile int DEF_PORT = 1337;

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
			
			RoutingTable.getInstance();
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
		
//		TaskManager.DO_TASK(new PostRemoteMessageTask("visitor15"));
		
//		TaskManager.DO_TASK(new ReceiveRemoteMessagesTask());
		
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
	
	public void setDefaultGateway(final String gateway) {
		DEFAULT_GATEWAY = gateway;
	}
	
	public String getDefaultGateway() {
		return DEFAULT_GATEWAY;
	}
	
	private void tryFindDefaultGateway() {
		TaskManager.DoTaskOnCurrentThread(new FindDefaultGatewayTask(), this);
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
	
	public static String GetDefaultGateway() {
		return DEFAULT_GATEWAY;
	}

	public void testCode() {
		int count = 0;
		boolean success;
		do {
			count++;
			success = TaskManager.DO_TASK(new SimpleAbstractTask("MESSAGE TASK") {

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
					System.out.println(this.getClass().getSimpleName() + " finished");
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

			});

		} while (count < 1);
	}

	@Override
	public void onTaskStart(Task task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAtomicTaskStart(Task task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTaskProgress(Task task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTaskFinished(Task task) {
		setDefaultGateway(task.getStringData());
	}
}
