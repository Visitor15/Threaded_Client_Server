package com.project.server;

public interface IDCServlet {

	public boolean registerServlet(final boolean autoStart);
	
	public boolean startServlet();
	
	public boolean stopServlet();
	
	public void respondToRequest();
	
	public void receiveRequest();
	
	public void checkResponses();
	
	public void sendResponse();
}
