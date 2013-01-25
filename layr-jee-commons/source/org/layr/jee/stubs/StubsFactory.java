package org.layr.jee.stubs;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.layr.jee.commons.JEERequestContext;

public class StubsFactory {

	public static ServletContext createServletContext() {
		ServletContextStub servletContext = new ServletContextStub();
		servletContext.setContextPath("test");
		return servletContext;
	}

	public static JEERequestContext createFullRequestContext( ) {
		ServletContext servletContext = createServletContext();
		return createRequestContext(servletContext);
	}

	public static JEERequestContext createRequestContext(ServletContext servletContext) {
		ServletRequest servletRequest = new HttpServletRequestStub();
		ServletResponse servletResponse = new HttpServletResponseStub();
		return JEERequestContext.createRequestContext(servletRequest, servletResponse, servletContext);
	}

}
