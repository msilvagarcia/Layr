package layr.routing.api;

import java.io.IOException;
import java.util.Map;

import static layr.commons.StringUtil.*;
import layr.engine.RequestContext;
import layr.engine.expressions.URLPattern;

public class Request {

	RequestContext requestContext;
	String routePattern;

	Map<String, String> pathParameters;
	Map<String, String> requestParameters;
	
	public Request(RequestContext requestContext, String routePattern) {
		this.requestContext = requestContext;
		this.routePattern = routePattern;
		memorizeParameters();
	}

	void memorizeParameters() {
		pathParameters = extractPathParameters();
		requestParameters = requestContext.getRequestParameters();
	}

	public Map<String, String> extractPathParameters() {
		return new URLPattern().extractMethodPlaceHoldersValueFromURL(
				routePattern,
				requestContext.getRequestURI() );
	}

	public Object getValue(RouteParameter parameter) throws IOException {
		String value = getParameterValue( parameter );
		if ( isEmpty(value) )
			return null;
		Object convertedValue = requestContext.convert( value, parameter.targetClazz );
		return convertedValue;
	}

	public String getParameterValue( RouteParameter parameter ) {
		if ( parameter instanceof PathRouteParameter )
			return pathParameters.get( parameter.name );
		if ( parameter instanceof QueryRouteParameter )
			return requestParameters.get( parameter.name );
		return null;
	}
}
