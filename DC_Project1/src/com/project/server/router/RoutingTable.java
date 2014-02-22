package com.project.server.router;

import java.util.HashMap;

import com.project.framework.Task;
import com.project.tasks.FindDefaultGatewayTask;
import com.project.tasks.ITaskCallback;
import com.project.tasks.TaskManager;

public class RoutingTable implements ITaskCallback {

	private static volatile RoutingTable m_Instance;

	private static HashMap<String, Client> m_ClientMap;
	private static HashMap<String, Server> m_ServerMap;

	private RoutingTable() {
		m_ClientMap = new HashMap<String, Client>();
		m_ServerMap = new HashMap<String, Server>();
		m_Instance = this;
	}

	public static synchronized RoutingTable getInstance() {
		if (m_Instance == null) {
			new RoutingTable();
		}

		return m_Instance;
	}

	public boolean registerClient(final Client c) {

		if (!m_ClientMap.containsKey(c.getUsername())) {

			m_ClientMap.put(c.getUsername(), c);

			return true;
		}

		return false;
	}

	public boolean registerServer(final Server s) {
		if (!m_ServerMap.containsKey(s.getCurrentIP())) {

			m_ServerMap.put(s.getHostname(), s);

			return true;
		}

		return false;
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
		
	}

}
