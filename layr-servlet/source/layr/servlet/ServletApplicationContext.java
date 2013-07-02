package layr.servlet;

import javax.servlet.ServletContext;

import layr.routing.lifecycle.DefaultApplicationContextImpl;

public class ServletApplicationContext extends DefaultApplicationContextImpl {

	ServletContext servletContext;

	public ServletApplicationContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	public Object getAttribute(String name) {
		return servletContext.getAttribute(name);
	}
	
	@Override
	public void setAttribute(String name, Object value) {
		servletContext.setAttribute(name, value);
	}
}
