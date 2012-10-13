package layr.core.test.stubs;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import layr.core.LayrFactory;
import layr.core.RequestContext;
import layr.core.servlet.LayrInitializer;
import layr.core.test.ManuallyClassPathReader;

public class StubsFactory {

	public static ServletContext createServletContext() throws IOException, ClassNotFoundException, ServletException {
		ServletContextStub servletContext = new ServletContextStub();
		servletContext.setContextPath("test");

		LayrInitializer initializer = new LayrInitializer();
		Set<Class<?>> read = new ManuallyClassPathReader().read();
		initializer.onStartup(read, servletContext);
		return servletContext;
	}

	public static RequestContext createFullRequestContext() throws IOException, ClassNotFoundException, ServletException {
		ServletContext servletContext = createServletContext();
		ServletRequest servletRequest = new HttpServletRequestStub();
		ServletResponse servletResponse = new HttpServletResponseStub();
		return LayrFactory.createRequestContext(servletRequest, servletResponse, servletContext);
	}
	
}
