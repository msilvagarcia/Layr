package layr.routing;

import java.util.HashMap;
import java.util.Map;

import layr.routing.impl.StubRequestContext;

public class RoutingTestSupport {

	LifeCycle lifeCycle;

	public RoutingTestSupport() {
		super();
	}

	public void setRequestURI(String uri) {
		String[] uriAndParams = split( uri, "\\?" );
		StubRequestContext requestContext = getRequestContext();
		StubRequestContext stubRequestContext = requestContext;
		stubRequestContext.setRequestURI( uriAndParams[0] );
		stubRequestContext.setRequestParameters( new HashMap<String, String>() );
		if ( uriAndParams.length == 2 )
			stubRequestContext.getRequestParameters().putAll( parseURIParams( uriAndParams[1] ) );
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

	public StubRequestContext getRequestContext() {
		return (StubRequestContext)lifeCycle.getRequestContext();
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

}
