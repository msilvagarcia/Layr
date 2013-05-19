package layr.routing.lifecycle;

import java.io.IOException;
import java.util.Map;

import layr.engine.RequestContext;
import layr.engine.expressions.URLPattern;
import layr.routing.api.ApplicationContext;
import layr.routing.api.DataProvider;
import layr.routing.exceptions.RoutingException;

public class Request {

	ApplicationContext applicationContext;
	RequestContext requestContext;
	String routePattern;

	Map<String, String> pathParameters;
	Map<String, String> requestParameters;
	
	public Request(ApplicationContext applicationContext, RequestContext requestContext, String routePattern) {
		this.applicationContext = applicationContext;
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

	public Object getValue(HandledParameter parameter) throws IOException {
		Object value = getParameterValue( parameter );
		if ( value == null )
			return null;
		if ( value instanceof String )
			return requestContext.convert( (String)value, parameter.getTargetClazz() );
		return value;
	}

	public Object getParameterValue( HandledParameter parameter ) throws RoutingException {
		if ( parameter instanceof PathHandledParameter )
			return pathParameters.get( parameter.getName() );
		if ( parameter instanceof QueryHandledParameter )
			return requestParameters.get( parameter.getName() );
		if ( parameter instanceof DataHandledParameter )
			return createDataHandledObject( parameter );
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Object createDataHandledObject(HandledParameter parameter) throws RoutingException {
		try {
			String canonicalName = parameter.getTargetClazz().getCanonicalName();
			Class<DataProvider> dataProviderClass = applicationContext.getRegisteredDataProviders().get(canonicalName);
			DataProvider dataProvider = (DataProvider<?>)dataProviderClass.newInstance();
			return dataProvider.newDataInstance(applicationContext, requestContext);
		} catch ( Throwable t ){
			throw new RoutingException(t);
		}
	}
}
