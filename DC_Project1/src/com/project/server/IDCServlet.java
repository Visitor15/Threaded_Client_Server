package com.project.server;

public interface IDCServlet {

	public void checkResponses();

	public void receiveRequest();

	public boolean registerServlet(final boolean autoStart);

	public void respondToRequest();

	public void sendResponse();

	public boolean startServlet();

	public boolean stopServlet();
}
