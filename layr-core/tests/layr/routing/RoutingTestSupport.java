package layr.routing;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import layr.api.RequestContext;
import layr.exceptions.NotFoundException;
import layr.exceptions.RoutingInitializationException;
import layr.routing.impl.StubRequestContext;
import layr.routing.impl.StubRoutingBootstrap;
import layr.routing.lifecycle.ApplicationContext;
import layr.routing.lifecycle.DefaultApplicationContextImpl;
import layr.routing.lifecycle.ExceptionHandlerListener;
import layr.routing.lifecycle.LifeCycle;

import org.junit.Before;

public abstract class RoutingTestSupport {

	LifeCycle lifeCycle;
	DefaultApplicationContextImpl configuration;
	RequestContext requestContext;
	
	@Before
	public void setup() throws Exception {
		try {
			StubRoutingBootstrap routingBootstrap = new StubRoutingBootstrap();
			setConfiguration( (DefaultApplicationContextImpl)routingBootstrap.configure( classes() ) );
			recreateLifeCycle();
		} catch (RoutingInitializationException e) {
			e.printStackTrace();
		}
	}

	abstract Set<Class<?>> classes();
	
	abstract LifeCycle createLifeCycle() throws Exception;

	public void setRequestURI(String uri) {
		String[] uriAndParams = split( uri, "\\?" );
		StubRequestContext requestContext = getRequestContext();
		StubRequestContext stubRequestContext = requestContext;
		stubRequestContext.setRequestURI( uriAndParams[0] );
		stubRequestContext.setRequestParameters( new HashMap<String, String>() );
		if ( uriAndParams.length == 2 )
			stubRequestContext.getRequestParameters()
				.putAll( parseURIParams( uriAndParams[1] ) );
	}

	public void setRequestMethod(String method) {
		StubRequestContext requestContext = getRequestContext();
		requestContext.setRequestHttpMethod( method );
	}
	
	public void get( String uri ) throws Exception {
		setRequestURI( uri );
		setRequestMethod( "GET" );
		run();
	}

	private void run() throws Exception {
		if (lifeCycle.canHandleRequest())
			lifeCycle.run();
		else
			throw new NotFoundException("not found");
	}
	
	public void post( String uri ) throws Exception {
		post( uri, null );
	}

	public void post( String uri, String body ) throws Exception {
		setRequestURI( uri );
		if ( body != null && !body.isEmpty() )
			getRequestContext().setRequestInputStream(new ByteArrayInputStream(body.getBytes()));
		setRequestMethod( "POST" );
		run();
	}
	
	public void put( String uri ) throws Exception {
		put( uri, null, null );
	}

	public void put( String uri, String body, String contentType ) throws Exception {
		setRequestURI( uri );
		if ( body != null && !body.isEmpty() )
			getRequestContext().setRequestInputStream(new ByteArrayInputStream(body.getBytes()));
		if ( contentType != null && !contentType.isEmpty() )
			getRequestContext().setContentType(contentType);
		setRequestMethod( "PUT" );
		run();
	}
	
	public void delete( String uri ) throws Exception {
		setRequestURI( uri );
		setRequestMethod( "DELETE" );
		run();
	}

	public StubRequestContext getRequestContext() {
		return (StubRequestContext) requestContext;
	}
	
	public void recreateLifeCycle() throws Exception{
		requestContext = createRequestContext();
		lifeCycle = createLifeCycle();
		lifeCycle.onFail( new ExceptionHandlerListener(getConfiguration(), getRequestContext()) );
	}

	public StubRequestContext createRequestContext() {
		StubRequestContext requestContext = new StubRequestContext();
		requestContext.setCache( configuration.getCache() );
		requestContext.setRegisteredTagLibs( configuration.getRegisteredTagLibs() );
		requestContext.setDefaultResource( configuration.getDefaultResource() );
		return requestContext;
	}

	String[] split(String uri, String divider) {
		if ( !uri.contains( divider.replace( "\\", "" ) ) )
			return new String[]{ uri };
		return uri.split(divider);
	}

	Map<String, String> parseURIParams(String params) {
		Map<String, String> uriParams = new HashMap<String, String>();
		for ( String param : split( params, "&" ) ) {
			String[] keyValue = param.split("=");
			uriParams.put( keyValue[0], keyValue[1] );
		}
		return uriParams;
	}
	
	public void setLifeCycle(LifeCycle lifeCycle) {
		this.lifeCycle = lifeCycle;
	}

	public ApplicationContext getConfiguration() {
		return configuration;
	}

	public void setConfiguration(DefaultApplicationContextImpl configuration) {
		this.configuration = configuration;
	}
}
