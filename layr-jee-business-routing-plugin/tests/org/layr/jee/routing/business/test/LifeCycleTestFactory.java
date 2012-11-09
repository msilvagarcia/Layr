package org.layr.jee.routing.business.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.layr.commons.Reflection;
import org.layr.jee.commons.JEEConfiguration;
import org.layr.jee.commons.JEERequestContext;
import org.layr.jee.routing.business.JEEBusinessRoutingConfiguration;
import org.layr.jee.routing.business.JEEBusinessRoutingRequestContext;
import org.layr.jee.routing.business.RequestLifeCycle;
import org.layr.jee.routing.business.Route;
import org.layr.jee.stubs.StubsFactory;


public class LifeCycleTestFactory {

	public static Object invokeCurrentRequestMethod( RequestLifeCycle lifeCycle, Object resource ) throws ServletException,
			IOException, IllegalAccessException, InvocationTargetException {
		lifeCycle.setRoutes( Reflection.extractAnnotatedMethodsFor(Route.class, resource) );
		Method actionMethod = lifeCycle.getRouteMethod();
		Object[] parameters = lifeCycle.retrieveRouteMethodParametersFromRequest(actionMethod);
		return actionMethod.invoke(resource, parameters);
	}
	
	public static RequestLifeCycle initializeLifeCycle () throws IOException, ClassNotFoundException, ServletException {
		ServletContext servletContext = StubsFactory.createServletContext();
		servletContext.setAttribute( JEEConfiguration.class.getCanonicalName() , new JEEConfiguration(servletContext));
		servletContext.setAttribute( JEEBusinessRoutingConfiguration.class.getCanonicalName() , new JEEBusinessRoutingConfiguration(servletContext));
		JEERequestContext requestContext = StubsFactory.createRequestContext( servletContext );
		RequestLifeCycle lifeCycle = new RequestLifeCycle();
		lifeCycle.setRequestContext(
				JEEBusinessRoutingRequestContext.createRequestContext(
				requestContext.getRequest(),
				requestContext.getResponse(),
				requestContext.getServletContext()));
		return lifeCycle;
	}
}
