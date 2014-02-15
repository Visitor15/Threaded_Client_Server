package com.project.server;

public interface IServletCallback {

	public <T extends IDCServlet> void onRegisterServlet(final T servlet);
	
	public <T extends IDCServlet> void onStartServlet(final T servlet);
	
	public <T extends IDCServlet> void onServletEvent(final T servlet);
	
	public <T extends IDCServlet> void onFinishServlet(final T servlet);
}
