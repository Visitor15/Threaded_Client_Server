package com.project.server;

import java.util.ArrayList;

import com.project.tasks.TaskManager;


public class ServletManager {

	private static ServletManager m_Instance;
	
	private ArrayList<DCServlet> activeServlets;
	
	private ArrayList<DCServlet> unactiveServlets;
	
	private ServletManager() {
		m_Instance = this;
		
		activeServlets = new ArrayList<DCServlet>();
		unactiveServlets = new ArrayList<DCServlet>();
	}
	
	public static synchronized <T extends DCServlet> boolean REGISTER_SERVLET(final T servlet) {
		
		if(m_Instance == null) {
			new ServletManager();
		}
		
		return m_Instance.startServlet(servlet);
	}
	
	private <T extends DCServlet> boolean startServlet(final T servlet) {
		
		System.out.println("Starting servlet: " + servlet.getTaskId());
		
		if(TaskManager.DO_TASK(servlet)) {
			activeServlets.add(servlet);
			
			return true;
		}
		else {
			unactiveServlets.add(servlet);
		}
		
		return false;
	}
}
