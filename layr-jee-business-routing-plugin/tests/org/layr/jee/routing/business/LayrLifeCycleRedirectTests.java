package org.layr.jee.routing.business;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;
import org.layr.jee.stubs.HttpServletRequestStub;

public class LayrLifeCycleRedirectTests {
	
	private RequestLifeCycleStub lifeCycle;

	@Before
	public void setup() throws SecurityException, NoSuchMethodException, IOException, ClassNotFoundException, ServletException{
		lifeCycle = RequestLifeCycleStub.initializeLifeCycle();
		lifeCycle.targetInstance = new FakeResourceClass();
		JEEBusinessRoutingRequestContext requestContext = lifeCycle.getRequestContext();
		requestContext.setWebResourceRootPath("fake/resource/");
		((HttpServletRequestStub)requestContext.getRequest()).setContextPath("/application/");
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
	
	@Test
	public void grantThatRunLifeCycleMethodDoRedirectionAsExpected() throws ServletException, IOException{
		lifeCycle.setRequestURL("fake/resource/redirect/?url=http://layr.org");
		String location = lifeCycle.runAndRetrieveNewRedirectedLocation();
		assertEquals("http://layr.org", location);
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
		lifeCycle.redirectToResource(url);
		return lifeCycle.lastRedirectedUri;
	}

	public Method extractStubTargetMethod() throws SecurityException, NoSuchMethodException{
		return FakeResourceClass.class.getDeclaredMethod("method");
	}

	public class FakeResourceClass{
		String urlToRedirect;
		
		@Route(redirectTo="#{urlToRedirect}")
		public void redirect(
			@Parameter("url") String url){
			urlToRedirect = url;
		}
	}

}
