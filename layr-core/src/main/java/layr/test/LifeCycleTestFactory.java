package layr.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import layr.LayrFactory;
import layr.LifeCycle;
import layr.RequestContext;
import layr.annotation.Action;
import layr.servlet.LayrInitializer;
import layr.util.Reflection;

public class LifeCycleTestFactory {

	public static ServletContext createServletContext() throws IOException, ClassNotFoundException, ServletException {
		ServletContextStub servletContext = new ServletContextStub();
		servletContext.setContextPath("test");

		LayrInitializer initializer = new LayrInitializer();
		initializer.manuallyInitialize(servletContext);
		return servletContext;
	}

	public static RequestContext createFullRequestContext() throws IOException, ClassNotFoundException, ServletException {
		ServletContext servletContext = createServletContext();
		ServletRequest servletRequest = new HttpServletRequestStub();
		ServletResponse servletResponse = new HttpServletResponseStub();
		return LayrFactory.createRequestContext(servletRequest, servletResponse, servletContext);
	}

	public static Object invokeCurrentRequestMethod( LifeCycle lifeCycle, Object resource ) throws ServletException,
			IOException, IllegalAccessException, InvocationTargetException {
		lifeCycle.setActions( Reflection.extractAnnotatedMethodsFor(Action.class, resource) );
		Method actionMethod = lifeCycle.getActionMethod();
		Object[] parameters = lifeCycle.retrieveActionMethodParametersFromRequest(actionMethod);
		return actionMethod.invoke(resource, parameters);
	}
	
	public static LifeCycle initializeLifeCycle () throws IOException, ClassNotFoundException, ServletException {
		RequestContext layrContext = LifeCycleTestFactory.createFullRequestContext();
		LifeCycle lifeCycle = new LifeCycle();
		lifeCycle.setLayrRequestContext(layrContext);
		return lifeCycle;
	}
	
}
