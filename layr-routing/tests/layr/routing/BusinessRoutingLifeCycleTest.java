package layr.routing;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import layr.routing.impl.NullPointerExceptionHandler;
import layr.routing.impl.RequestContextDataProvider;
import layr.routing.lifecycle.BusinessRoutingLifeCycle;
import layr.routing.lifecycle.LifeCycle;
import layr.routing.sample.HelloResource;
import layr.routing.sample.HomeResource;

import org.junit.Test;

public class BusinessRoutingLifeCycleTest extends RoutingTestSupport {

	static final int MANY_TIMES = 1000;

	public LifeCycle createLifeCycle(){
		return new BusinessRoutingLifeCycle( getConfiguration(), getRequestContext() );
	}

	Set<Class<?>> classes(){
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add( HelloResource.class );
		classes.add( HomeResource.class );
		classes.add( NullPointerExceptionHandler.class );
		classes.add( RequestContextDataProvider.class );
		return classes;
	}

	@Test
	public void grantThatRenderHome() throws Exception {
		get( "/" );
		assertEquals( "<p><button class=\"btn\">Premium Panel</button><p>:</p></p>", getRequestContext().getBufferedWroteContentToOutput() );
	}

	@Test
	public void grantThatSendGetAndRenderTemplateAsExpected() throws Exception {
		get( "/hello/world/1234?requestParam=12.5" );
		assertEquals( "<p>1234:12.5</p>", getRequestContext().getBufferedWroteContentToOutput() );
	}

	@Test
	public void grantThatHandleNullPointerExceptionAsExpected() throws Exception {
		get( "/hello/handled/error" );
		assertEquals( "/fail/", getRequestContext().getRedirectedURL() );
	}

	@Test
	public void grantThatSendPutAndRenderTemplateAsExpected() throws Exception{
		put( "/hello/world/1234?requestParam=12.5" );
		assertEquals( "<p>1234:12.5</p>", getRequestContext().getBufferedWroteContentToOutput() );
	}

	@Test
	public void grantThatSendPostAndRenderTemplateAsExpected() throws Exception{
		post( "/hello/world" );
		assertEquals( "<p>/:/home/</p>", getRequestContext().getBufferedWroteContentToOutput() );
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
			get( "/hello/world/1234?requestParam=12.5" );
		}
	}

}
