package layr.routing;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import layr.routing.impl.StubRoutingBootstrap;

import org.junit.Before;
import org.junit.Test;

public class BusinessRoutingLifeCycleTest extends RoutingTestSupport {

	static final int MANY_TIMES = 1000;
	Configuration configuration;

	public BusinessRoutingLifeCycleTest() throws RoutingInitializationException {
		RoutingBootstrap routingBootstrap = new StubRoutingBootstrap();
		configuration = routingBootstrap.configure( exposedRoute() );
	}

	@Before
	public void setup() throws RoutingInitializationException{
		lifeCycle = new BusinessRoutingLifeCycle( configuration );
	}

	@Test
	public void grantThatSendGetAndRenderTemplateAsExpected() throws Exception {
		get( "/hello/world/1234?requestParamOnBody=12.5" );
		assertEquals( "<p>1234:12.5</p>", getRequestContext().getBufferedWroteContentToOutput() );
	}

	@Test
	public void grantThatSendPutAndRenderTemplateAsExpected() throws Exception{
		put( "/hello/world/1234?requestParamOnBody=12.5" );
		assertEquals( "<p>1234:12.5</p>", getRequestContext().getBufferedWroteContentToOutput() );
	}

	@Test
	public void grantThatSendPostAndRedirectToURLAsExpected() throws Exception{
		post( "/hello/world/12345/234/?isSomething=true" );
		assertEquals( "/response/12345/234.0/true/", getRequestContext().getRedirectedURL() );
	}

	@Test
	public void grantThatReceive204WhenNoTemplateIsDefined() throws Exception{
		delete( "/hello/world/" );
		assertEquals( 204, getRequestContext().getStatusCode() );
	}

	@Test( timeout=2000 )
	public void stressTestFiveTimes() throws Exception{
		for ( int i=0; i<5; i++ )
			stressTest();
	}

	public void stressTest() throws Exception{
		for ( int i=0; i<MANY_TIMES; i++ ){
			setup();
			get( "/hello/world/1234?requestParamOnBody=12.5" );
		}
	}

	Set<Class<?>> exposedRoute(){
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add( HelloResource.class );
		return classes;
	}

}
