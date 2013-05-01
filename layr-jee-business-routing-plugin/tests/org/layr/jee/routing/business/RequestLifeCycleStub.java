package org.layr.jee.routing.business;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import layr.commons.Reflection;
import layr.engine.AbstractRequestContext;
import layr.engine.components.ComponentFactory;
import layr.routing.StubConfiguration;
import layr.routing.annotations.Route;
import layr.routing.jee.JEERequestContext;

import org.layr.jee.routing.business.JEEBusinessRoutingConfiguration;
import org.layr.jee.routing.business.JEEBusinessRoutingRequestContext;
import org.layr.jee.routing.business.RequestLifeCycle;
import org.layr.jee.stubs.HttpServletRequestStub;
import org.layr.jee.stubs.HttpServletResponseStub;
import org.layr.jee.stubs.StubsFactory;

public class RequestLifeCycleStub extends RequestLifeCycle {
	
	String lastRedirectedUri;

	@Override
	public void redirect(String uri) throws IOException {
		lastRedirectedUri = uri;
	}

	public Object invokeCurrentRequestMethod( Object resource ) throws ServletException,
			IOException, IllegalAccessException, InvocationTargetException {
		setRoutes( Reflection.extractAnnotatedMethodsFor(Route.class, resource) );
		Method actionMethod = getRouteMethod();
		Object[] parameters = retrieveRouteMethodParametersFromRequest(actionMethod);
		return actionMethod.invoke(resource, parameters);
	}

	public String runMethodAndReturnWroteOutputData() throws ServletException, IOException{
		run();
        return getResponseAsStub().readWroteOutput();
	}
	
	public String runAndRetrieveNewRedirectedLocation() throws ServletException, IOException{
		run();
		return lastRedirectedUri;
	}

	public void setRequestURL(String url) {
		getRequestAsStub().setRequestURL(url);
	}

	public HttpServletResponseStub getResponseAsStub() {
		return (HttpServletResponseStub)getRequestContext().getResponse();
	}

	public HttpServletRequestStub getRequestAsStub() {
		return (HttpServletRequestStub)getRequestContext().getRequest();
	}

	public static RequestLifeCycleStub initializeLifeCycle () throws IOException, ClassNotFoundException, ServletException {
		ServletContext servletContext = StubsFactory.createServletContext();
		servletContext.setAttribute( StubConfiguration.class.getCanonicalName() , new StubConfiguration(servletContext));
		servletContext.setAttribute( JEEBusinessRoutingConfiguration.class.getCanonicalName() , new JEEBusinessRoutingConfiguration(servletContext));
		JEERequestContext requestContext = StubsFactory.createRequestContext( servletContext );
		
		RequestLifeCycleStub lifeCycle = new RequestLifeCycleStub();
		lifeCycle.setRequestContext(createBusinessRoutingContext(requestContext));
		
		lifeCycle.getRequestContext().getRegisteredTagLibs();
		
		return lifeCycle;
	}

	public static JEEBusinessRoutingRequestContext createBusinessRoutingContext(
			JEERequestContext requestContext) {
		JEEBusinessRoutingRequestContext businessRequestContext = JEEBusinessRoutingRequestContext
			.createRequestContext(
				requestContext.getRequest(),
				requestContext.getResponse(),
				requestContext.getServletContext());
		Map<String, ComponentFactory> registeredTagLibs = new HashMap<String, ComponentFactory>();
		AbstractRequestContext.populateWithDefaultTagLibs(registeredTagLibs );
		businessRequestContext.getConfiguration().setRegisteredTagLibs(registeredTagLibs);
		return businessRequestContext;
	}
}
