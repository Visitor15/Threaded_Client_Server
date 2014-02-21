package com.project.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;

import com.project.framework.Task;
import com.project.server.router.Client;
import com.project.server.router.RoutingTable;

public class ClientRegistrationServlet extends DCServlet {

	public static final int PORT = 1337;

	public ClientRegistrationServlet(final boolean autoStart,
			final IServletCallback callback) {
		super(SERVLET_TYPE.REGISTRATION_SERVLET, autoStart, callback);

//		if (autoStart) {
//			startServlet();
//		}

		// init();
	}

	private void init() {
		try {
			System.out.println("INIT");
			m_ServerSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void respondToRequest() {
		// System.out.println("Client Registration responding.");
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


			System.out.println(this.getClass().getSimpleName() + " EXECUTING");

			if (m_ServerSocket == null) {
				try {
					m_ServerSocket = new ServerSocket(PORT);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			do {

				String m_Input;
				String response;

				try {
					// m_ServerSocket.setSoTimeout(1000);
					m_RecievingSocket = m_ServerSocket.accept();
					PrintWriter out = new PrintWriter(
							m_RecievingSocket.getOutputStream(), true);
					BufferedReader in = new BufferedReader(
							new InputStreamReader(
									m_RecievingSocket.getInputStream()));

					while ((m_Input = in.readLine()) != null) {
						System.out.println("ECHO FROM REGISTRATION SERVLET: "
								+ m_Input);

						break;
					}

					Client client = new Client();
					client.setUsername(m_Input);
					client.setPort(1337);

					System.out.println("Got client: " + client.getUsername());

					if (RoutingTable.getInstance().registerClient(client)) {
						response = "200";
					} else {
						response = "401";
					}

					System.out.println("Response: " + response);

					out.println(response);

					out.flush();

					out.close();
					in.close();

					// try {
					// Thread.sleep(1000);
					// } catch (InterruptedException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} while (isExecuting());

			onFinished();
		
	}

	@Override
	public void onProgressUpdate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinished() {
		System.out.println("Client Registration shutting down.");

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
