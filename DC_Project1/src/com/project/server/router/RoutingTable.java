package com.project.server.router;

import java.util.HashMap;
import java.util.Set;

import com.project.framework.Task;
import com.project.tasks.ITaskCallback;

public class RoutingTable implements ITaskCallback {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2909550583271953761L;

	private static volatile RoutingTable m_Instance;

	private static HashMap<String, Client> m_ClientMap;
	private static HashMap<String, Server> m_ServerMap;

	private static Server primaryServer;

	public static synchronized RoutingTable getInstance() {
		if (m_Instance == null) {
			new RoutingTable();
		}

		return m_Instance;
	}

	private RoutingTable() {
		m_ClientMap = new HashMap<String, Client>();
		m_ServerMap = new HashMap<String, Server>();
		m_Instance = this;
	}

	public Client getClientAtIndex(final int pos) {
		Set<String> keys = m_ClientMap.keySet();

		int count = 0;
		for (String s : keys) {
			if (count == pos) {
				return m_ClientMap.get(s);
			}
			count++;
		}

		return null;
	}

	public Client getClientByIP(String ip) {
		Set<String> keys = m_ClientMap.keySet();

		for (String s : keys) {
			if (s.equals(ip)) {
				return m_ClientMap.get(s);
			}
		}

		return null;
	}

	public Server getPrimaryServer() {
		return primaryServer;
	}

	public Server getServerAtIndex(final int pos) {
		Set<String> keys = m_ServerMap.keySet();

		int count = 0;
		for (String s : keys) {
			if (count == pos) {
				return m_ServerMap.get(s);
			}
			count++;
		}

		return null;
	}

	public Server getServerByHostname(String hostname) {
		Set<String> keys = m_ServerMap.keySet();

		for (String s : keys) {
			if (s.equals(hostname)) {
				return m_ServerMap.get(s);
			}
		}

		return null;
	}

	@Override
	public void onAtomicTaskStart(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskFinished(Task task) {

	}

	@Override
	public void onTaskProgress(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTaskStart(Task task) {
		// TODO Auto-generated method stub

	}

	public boolean registerClient(final Client c) {

		if (!m_ClientMap.containsKey(c.getCurrentIP())) {

			m_ClientMap.put(c.getCurrentIP(), c);

			return true;
		}

		return false;
	}

	public boolean unRegisterClient(final Client c) {

		if (m_ClientMap.size() > 0) {

			Client client = m_ClientMap.remove(c);

			if (client != null) {
				return true;
			}
		}

		return false;
	}

	public boolean registerServer(final Server s) {
		if (!m_ServerMap.containsKey(s.getCurrentIP())) {

			primaryServer = s;
			m_ServerMap.put(s.getCurrentIP(), s);

			return true;
		}

		return true;
	}

	public void setPrimaryServer(Server server) {

	}
}
