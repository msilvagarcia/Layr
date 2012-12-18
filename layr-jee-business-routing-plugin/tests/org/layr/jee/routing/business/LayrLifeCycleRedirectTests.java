package org.layr.jee.routing.business;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class LayrLifeCycleRedirectTests {
	
	private RequestLifeCycleStub requestLifeCycle;
	private Method stubRequestMethod;

	@Before
	public void setup() throws SecurityException, NoSuchMethodException{
		requestLifeCycle = new RequestLifeCycleStub();
		requestLifeCycle.targetInstance = new FakeResourceClass();
		requestLifeCycle.requestContext = mockRequestContext();
		stubRequestMethod = extractStubTargetMethod();
	}

	@Test
	public void grantThatRedirectToInsideResource()
			throws IllegalArgumentException, IOException, ServletException,
				   IllegalAccessException, InvocationTargetException {
		String url = createRedirectUrl("do/something/inside/resource/");
		assertEquals("/application/fake/resource/do/something/inside/resource/", url);
	}

	@Test
	public void grantThatRedirectToOutsideResource()
			throws IllegalArgumentException, IOException, ServletException,
				   IllegalAccessException, InvocationTargetException {
		String url = createRedirectUrl("/do/something/outside/resource/");
		assertEquals("/application/do/something/outside/resource/", url);
	}

	@Test
	public void grantThatRedirectToOutsideApplication()
			throws IllegalArgumentException, IOException, ServletException,
				   IllegalAccessException, InvocationTargetException {
		String url = createRedirectUrl("http://layr.org");
		assertEquals("http://layr.org", url);
	}

	public JEEBusinessRoutingRequestContext mockRequestContext(){
		JEEBusinessRoutingRequestContext requestContext = mock(JEEBusinessRoutingRequestContext.class);

		when(requestContext.getRequestParamsFromRoutePattern())
			.thenReturn( new HashMap<String, String>() );

		when(requestContext.getWebResourceRootPath())
			.thenReturn("fake/resource/");

		when(requestContext.getApplicationRootPath())
			.thenReturn("/application/");

		return requestContext;
	}

	public String createRedirectUrl( String url ) 
			throws IllegalArgumentException, IOException, ServletException,
				   IllegalAccessException, InvocationTargetException {
		requestLifeCycle.redirectToResource(stubRequestMethod, url);
		String lastRedirectedUri = requestLifeCycle.lastRedirectedUri;
		return lastRedirectedUri;
	}
	
	public Method extractStubTargetMethod() throws SecurityException, NoSuchMethodException{
		return FakeResourceClass.class.getDeclaredMethod("method");
	}

	public class FakeResourceClass{
		public void method(){}
	}

}
