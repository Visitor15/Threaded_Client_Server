package com.project.server.router;

import java.util.HashMap;

public class RoutingTable {

	private static RoutingTable m_Instance;

	private static HashMap<String, Client> m_ClientMap;
	private static HashMap<String, Server> m_ServerMap;

	private RoutingTable() {
		m_ClientMap = new HashMap<String, Client>();
		m_Instance = this;
	}

	public static synchronized RoutingTable getInstance() {
		if (m_Instance == null) {
			new RoutingTable();
		}

		return m_Instance;
	}

	public boolean registerClient(final Client c) {

		if (!m_ClientMap.containsKey(c.getId())) {

			m_ClientMap.put(c.getId(), c);

			return true;
		}

		return false;
	}

	public boolean registerServer(final Server s) {
		if (!m_ServerMap.containsKey(s.getId())) {

			m_ServerMap.put(s.getId(), s);

			return true;
		}

		return false;
	}

}
