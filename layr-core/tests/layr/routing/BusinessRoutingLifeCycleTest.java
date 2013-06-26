package layr.routing;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import layr.org.codehaus.jackson.JSONWrapper;
import layr.routing.impl.NullPointerExceptionHandler;
import layr.routing.impl.RequestContextDataProvider;
import layr.routing.lifecycle.BusinessRoutingLifeCycle;
import layr.routing.lifecycle.LifeCycle;
import layr.routing.sample.HelloResource;
import layr.routing.sample.HomeResource;

import org.junit.Test;

public class BusinessRoutingLifeCycleTest extends RoutingTestSupport {

	static final int MANY_TIMES = 1200;

	public LifeCycle createLifeCycle(){
		return new BusinessRoutingLifeCycle( getConfiguration(), getRequestContext() );
	}

	Set<Class<?>> classes(){
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add( HelloResource.class );
		classes.add( HomeResource.class );
		classes.add( NullPointerExceptionHandler.class );
		classes.add( RequestContextDataProvider.class );
		classes.add( JSONWrapper.class );
		return classes;
	}

	@Test
	public void grantThatRenderHome() throws Exception {
		get( "/" );
		assertEquals( 200, getRequestContext().getStatusCode() );
		assertEquals( "text/html", getRequestContext().getContentType() );
		assertEquals( "<p><button class=\"btn\">Premium Panel</button><p>:</p></p>", getRequestContext().getBufferedWroteContentToOutput() );
	}

	@Test
	public void grantThatSendGetAndRenderTemplateAsExpected() throws Exception {
		get( "/hello/world/1234?requestParam=12.5" );
		assertEquals( 200, getRequestContext().getStatusCode() );
		assertEquals( "text/html", getRequestContext().getContentType() );
		assertEquals( "<p>1234:12.5</p>", getRequestContext().getBufferedWroteContentToOutput() );
	}

	@Test
	public void grantThatSendGetAndRenderTemplateThroughFilterObjectAsExpected() throws Exception {
		get( "/hello/world/filter/object?requestParam=12.5&pathParam=1234" );
		assertEquals( 200, getRequestContext().getStatusCode() );
		assertEquals( "text/html", getRequestContext().getContentType() );
		assertEquals( "<p>1234:12.5</p>", getRequestContext().getBufferedWroteContentToOutput() );
	}

	@Test
	public void grantThatSendPostAndRenderTemplateThroughBodyObjectAsExpected() throws Exception {
		put( "/hello/world/body/object", "{ \"requestParam\":12.5, \"pathParam\":1234}", "application/json" );
		assertEquals( 200, getRequestContext().getStatusCode() );
		assertEquals( "text/html", getRequestContext().getContentType() );
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
		assertEquals( 200, getRequestContext().getStatusCode() );
		assertEquals( "<p>1234:12.5</p>", getRequestContext().getBufferedWroteContentToOutput() );
	}

	@Test
	public void grantThatSendPostAndRenderTemplateAsExpected() throws Exception{
		post( "/hello/world" );
		assertEquals( 200, getRequestContext().getStatusCode() );
		assertEquals( "text/html", getRequestContext().getContentType() );
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

	@Test
	public void grantThatRenderJSonWithResponseBuilderAsExpected() throws Exception {
		get( "/hello/json/builder" );
		assertEquals( "application/json", getRequestContext().getContentType() );
		assertEquals( 200, getRequestContext().getStatusCode() );
		assertEquals( "{\"pathParam\":3336,\"requestParam\":5432.1}", getRequestContext().getBufferedWroteContentToOutput() );
	}

	@Test
	public void grantThatRenderJSonWithoutResponseBuilderAsExpected() throws Exception {
		get( "/hello/json/object" );
		assertEquals( 200, getRequestContext().getStatusCode() );
		assertEquals( "application/json", getRequestContext().getContentType() );
		assertEquals( "{\"pathParam\":3336,\"requestParam\":5432.1}", getRequestContext().getBufferedWroteContentToOutput() );
	}

	@Test
	public void grantThatRenderDifferentStatusCodeAsExpected() throws Exception{
		delete( "/hello/world/status/code" );
		assertEquals("", getRequestContext().getBufferedWroteContentToOutput());
		assertEquals( 201, getRequestContext().getStatusCode() );
		assertEquals( "/blah", getRequestContext().getResponseHeaders().get("Location"));
	}

	@Test//( timeout=2500 )
	public void stressTestFiveTimes() throws Exception{
		for ( int i=0; i<5; i++ )
			stressTest();
	}

	public void stressTest() throws Exception{
		for ( int i=0; i<MANY_TIMES; i++ ){
			recreateLifeCycle();
			get( "/hello/world/1234?requestParam=12.5" );
		}
	}

}
