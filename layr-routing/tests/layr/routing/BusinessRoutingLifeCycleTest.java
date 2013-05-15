package layr.routing;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import layr.routing.api.Configuration;
import layr.routing.exceptions.RoutingInitializationException;
import layr.routing.exceptions.UnhandledException;
import layr.routing.impl.StubRoutingBootstrap;
import layr.routing.service.BusinessRoutingLifeCycle;
import layr.routing.service.RoutingBootstrap;

import org.junit.Before;
import org.junit.Test;

public class BusinessRoutingLifeCycleTest extends RoutingTestSupport {

	static final int MANY_TIMES = 1000;
	Configuration configuration;

	public BusinessRoutingLifeCycleTest() throws RoutingInitializationException {
		RoutingBootstrap routingBootstrap = new StubRoutingBootstrap();
		configuration = routingBootstrap.configure( classes() );
	}

	@Before
	public void setup() throws RoutingInitializationException{
		lifeCycle = new BusinessRoutingLifeCycle( configuration );
	}

	@Test
	public void grantThatRenderHome() throws Exception {
		get( "/" );
		assertEquals( "<p><button class=\"btn\">Premium Panel</button><p>:</p></p>", getRequestContext().getBufferedWroteContentToOutput() );
	}

	@Test
	public void grantThatSendGetAndRenderTemplateAsExpected() throws Exception {
		get( "/hello/world/1234?requestParamOnBody=12.5" );
		assertEquals( "<p>1234:12.5</p>", getRequestContext().getBufferedWroteContentToOutput() );
	}

	@Test
	public void grantThatHandleNullPointerExceptionAsExpected() throws Exception {
		get( "/hello/handled/error" );
		assertEquals( "/fail/", getRequestContext().getRedirectedURL() );
	}

	@Test( expected=UnhandledException.class )
	public void grantThatCantHandleIOExceptinException() throws Exception {
		get( "/hello/unhandled/error" );
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

	@Test( timeout=2200 )
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

	Set<Class<?>> classes(){
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add( HelloResource.class );
		classes.add( HomeResource.class );
		classes.add( NullPointerExceptionHandler.class );
		return classes;
	}

}
