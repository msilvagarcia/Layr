package org.layr.jee.routing.business.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;
import org.layr.jee.routing.business.JEEBusinessRoutingRequestContext;
import org.layr.jee.routing.business.RequestLifeCycle;
import org.layr.jee.routing.business.sample.HelloResource;
import org.layr.jee.routing.business.sample.World;
import org.layr.jee.stubs.HttpServletRequestStub;

public class LayrLifeCycleTest {
	
	private HelloResource resource;
	private JEEBusinessRoutingRequestContext requestContext;
	private RequestLifeCycle lifeCycle;

	@Before
	public void setupRequest() throws IOException, ClassNotFoundException, ServletException {
		if (requestContext != null)
			return;
		
		lifeCycle = LifeCycleTestFactory.initializeLifeCycle();

		resource = new HelloResource();
		lifeCycle.setTargetInstance(resource);

		requestContext = lifeCycle.getRequestContext();
		requestContext.setWebResourceRootPath("hello");

		setRequestURL("/hello/sayHello");
		sendDefaultParameters();

		lifeCycle.bindParametersSentFromRequestAgainstWebResourceFields();
	}

	public void sendDefaultParameters() {
		HttpServletRequestStub servletRequest = (HttpServletRequestStub)requestContext.getRequest();
		HashMap<String, String> dictionary = new HashMap<String, String>();
		dictionary.put("hello.world", "Earth");
		dictionary.put("hello.realworld.name", "Moon");
		dictionary.put("hello.realworld.id", "12");
		dictionary.put("hello.size", "123499");
		dictionary.put("hello.today", "26/05/2012");
		dictionary.put("sum", "52.04");
		dictionary.put("name", "Test");
		dictionary.put("hello.countries", "[\"String1\",\"String2\"]");
		dictionary.put("worlds", "[{\"name\":\"Ramon Valdes\",\"hello\":{ \"countries\": [\"Brazil\",\"London\"]},\"id\":431},{\"name\":\"Roberto Bolaños\",\"id\":681}]");
		dictionary.put("hello.myWorld", "{\"name\":\"Ramon Valdes\",\"id\":431}");
		servletRequest.setParameters(dictionary);
	}

	public void setRequestURL(String url) {
		((HttpServletRequestStub)requestContext.getRequest()).setRequestURL(url);
	}

	public Object invokeCurrentRequestMethod() throws ServletException,
			IOException, IllegalAccessException, InvocationTargetException {
		return LifeCycleTestFactory.invokeCurrentRequestMethod(lifeCycle, resource);
	}

	@Test
	public void grantThatBindRequestedParameters ()
			throws ServletException, IOException {

		assertEquals("Earth", resource.getHello().getWorld());
		assertEquals("Moon", resource.getHello().getRealworld().getName());
		assertEquals((Long)12L, resource.getHello().getRealworld().getId());
		assertEquals((Integer)123499, resource.getHello().getSize());
		assertEquals(getTodayDate(), resource.getHello().getToday());
		assertEquals((Double)52.04, (Double)resource.getSum());
		assertNotNull(resource.getHello().getCountries());
		assertEquals(2, resource.getHello().getCountries().size());
		assertEquals("String1", resource.getHello().getCountries().get(0));
		assertEquals("String2", resource.getHello().getCountries().get(1));

		assertNotNull(resource.getHello().getMyWorld());
		assertNotNull("Ramon Valdes", resource.getHello().getMyWorld().getName());

		assertNotNull(resource.getWorlds());
		World world = resource.getWorlds().get(0);
		assertNotNull("Ramon Valdes", world.getName());
		assertNotNull("Brazil", world.getHello().getCountries().get(0));
	}

	@Test
	public void grantThatFindGeneralTemplate () {
		assertEquals("/hello.xhtml", lifeCycle.getGeneralTemplate());
	}

	@Test
	public void grantThatFoundTheMethodFoundTheParametersAndCanInvokeHim() throws ServletException, IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Object result = invokeCurrentRequestMethod();
		assertEquals("Hello World", result);
	}

	@Test
	public void grantThatFoundTheMethodWhenURLNotEndsWithSlash() throws ServletException, IOException, IllegalAccessException, InvocationTargetException {
		setRequestURL("/hello/sayHello");
		Object result = invokeCurrentRequestMethod();
		assertEquals("Hello World", result);
	}

	@Test
	public void grantThatFoundTheMethodWhenURLNotEndsWithSlashAndReceiveAnParameterThatIsNotMappepAsAttribute() throws ServletException, IOException, IllegalAccessException, InvocationTargetException {
		setRequestURL("/hello/add");
		Object result = invokeCurrentRequestMethod();
		assertEquals("Test", result);
	}

	@Test
	public void grantThatFoundTheMethodWhenURLNotEndsWithSlashAndReceiveAnParameterThatIsNotMappepAsAttributeAndReturnJSON()
			throws ServletException, IOException, IllegalAccessException, InvocationTargetException {
		setRequestURL("/hello/addAsJSON");
		Object result = invokeCurrentRequestMethod();
		assertEquals("Test", result);
	}

	@Test
	public void grantThatRunTheMethodWithParametersFromPattern() throws ServletException, IOException, IllegalAccessException, InvocationTargetException {
		setRequestURL("/say/123/something/");
		Object result = invokeCurrentRequestMethod();
		assertEquals("Id '123' and '12' arrived from request.", result);
	}

	private Date getTodayDate() {
		return new GregorianCalendar(2012, 4, 26).getTime();
	}
	
}
