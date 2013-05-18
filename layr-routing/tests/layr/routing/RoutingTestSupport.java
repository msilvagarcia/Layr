package layr.routing;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import layr.engine.RequestContext;
import layr.routing.api.ApplicationContext;
import layr.routing.exceptions.RoutingInitializationException;
import layr.routing.impl.StubRequestContext;
import layr.routing.impl.StubRoutingBootstrap;
import layr.routing.lifecycle.LifeCycle;
import layr.routing.lifecycle.RoutingBootstrap;

import org.junit.Before;

public abstract class RoutingTestSupport {

	LifeCycle lifeCycle;
	ApplicationContext configuration;
	RequestContext requestContext;

	abstract Set<Class<?>> classes();
	
	@Before
	public void setup() throws Exception {
		try {
			RoutingBootstrap routingBootstrap = new StubRoutingBootstrap();
			setConfiguration( routingBootstrap.configure( classes() ) );
			requestContext = createRequestContext();
			lifeCycle = createLifeCycle();
		} catch (RoutingInitializationException e) {
			e.printStackTrace();
		}
	}
	
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
		lifeCycle.run();
	}

	public void post( String uri ) throws Exception {
		setRequestURI( uri );
		setRequestMethod( "POST" );
		lifeCycle.run();
	}
	
	public void put( String uri ) throws Exception {
		setRequestURI( uri );
		setRequestMethod( "PUT" );
		lifeCycle.run();
	}
	
	public void delete( String uri ) throws Exception {
		setRequestURI( uri );
		setRequestMethod( "DELETE" );
		lifeCycle.run();
	}

	private RequestContext createRequestContext() {
		return configuration.createContext( null );
	}

	public StubRequestContext getRequestContext() {
		return (StubRequestContext)requestContext;
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

	public void setConfiguration(ApplicationContext configuration) {
		this.configuration = configuration;
	}
}
