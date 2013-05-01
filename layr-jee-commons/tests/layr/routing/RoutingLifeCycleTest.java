package layr.routing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import layr.engine.StubRequestContext;

import org.junit.Before;
import org.junit.Test;

public class RoutingLifeCycleTest {

	RoutingLifeCycle lifeCycle;
	
	@Test
	public void grantThatSendGetAndRenderTemplateAsExpected() throws NotFoundException, RoutingException{
		setRequestURI( "/hello/world/1234", "GET" );
		lifeCycle.run();
	}
	
	@Before
	public void setup() throws RoutingInitializationException{
		RoutingBootstrap routingBootstrap = new RoutingBootstrap();
		Configuration configuration = routingBootstrap.configure( exposedRoute() );
		lifeCycle = new RoutingLifeCycle( configuration );
	}
	
	Set<Class<?>> exposedRoute(){
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add( HelloResource.class );
		return classes;
	}

	void setRequestURI(String uri, String method) {
		String[] uriAndParams = split( uri, "?" );
		StubRequestContext requestContext = getRequestContext();
		StubRequestContext stubRequestContext = requestContext;
		stubRequestContext.setRequestURI( uriAndParams[0] );
		stubRequestContext.setRequestHttpMethod( method );
		stubRequestContext.setRequestParameters( new HashMap<String, String>() );
		if ( uriAndParams.length == 2 )
			stubRequestContext.getRequestParameters().putAll( parseURIParams( uriAndParams[1] ) );
	}

	StubRequestContext getRequestContext() {
		return (StubRequestContext)lifeCycle.requestContext;
	}

	String[] split( String uri, String divider ) {
		if ( !uri.contains( divider ) )
			return new String[]{ uri };
		return uri.split(divider);
	}
	
	Map<String, String> parseURIParams( String params ) {
		Map<String, String> uriParams = new HashMap<String, String>();
		for ( String param : split( params, "&" ) ) {
			String[] keyValue = param.split("=");
			uriParams.put( keyValue[0], keyValue[1] );
		}
		return uriParams;
	}

}
