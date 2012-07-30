package layr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;

import layr.sample.HelloResource;
import layr.test.LayrTest;
import layr.util.Dictionary;

import org.junit.Test;


public class LayrTestTest extends LayrTest<HelloResource> {

	@Override
	public void setupRequest() {
		sendParameters(new Dictionary<String, String>()
				.set("hello.world", "Earth")
				.set("hello.realworld.name", "Moon")
				.set("hello.realworld.id", "12")
				.set("hello.size", "123499")
				.set("hello.today", "26/05/2012")
				.set("sum", "52.04")
				.set("name", "Test")
				.set("hello.countries", "[\"String1\",\"String2\"]")
				.set("worlds", "[{\"name\":\"Ramon Valdes\",\"id\":431},{\"name\":\"Roberto Bolaños\",\"id\":681}]")
				.set("hello.myWorld", "{\"name\":\"Ramon Valdes\",\"id\":431}")
			);
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
		assertNotNull("Ramon Valdes", resource.getWorlds().get(0).getName());
	}

	@Test
	public void grantThatFindGeneralTemplate () {
		assertEquals("/hello.xhtml", lifeCycle.getGeneralTemplate());
	}

	@Test
	public void grantThatFoundTheMethodFoundTheParametersAndCanInvokeHim() throws ServletException, IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		setRequestURL("/hello/sayHello/");
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
