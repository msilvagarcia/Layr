package layr.routing.lifecycle;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import layr.api.DataProvider;
import layr.api.RequestContext;
import layr.commons.Reflection;
import layr.engine.expressions.URLPattern;
import layr.exceptions.RoutingException;

public class Request {

	ApplicationContext applicationContext;
	RequestContext requestContext;
	String routePattern;

	Map<String, String> pathParameters;
	Map<String, String> requestParameters;

	public Request(ApplicationContext applicationContext,
			RequestContext requestContext, String routePattern) {
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
				routePattern, requestContext.getRequestURI());
	}

	public Object getValue(HandledParameter parameter) throws IOException {
		Object value = getParameterValue(parameter);
		if (value == null)
			return null;
		if (value instanceof String)
			return requestContext.convert((String) value,
					parameter.getTargetClazz());
		return value;
	}

	public Object getParameterValue(HandledParameter parameter)
			throws RoutingException {
		if (parameter instanceof PathHandledParameter)
			return pathParameters.get(parameter.getName());
		if (parameter instanceof QueryHandledParameter)
			return requestParameters.get(parameter.getName());
		if (parameter instanceof DataHandledParameter)
			return createDataHandledObject(parameter);
		if (parameter instanceof QueryHandledParameters)
			return createFilterObjectFromParameter(parameter);
		return null;
	}

	public Object createFilterObjectFromParameter(HandledParameter parameter)
			throws RoutingException {
		try {
			Object instance = parameter.targetClazz.newInstance();
			for (String parameterName : requestParameters.keySet())
				setAttribute(instance, parameterName,
						requestParameters.get(parameterName));
			return instance;
		} catch (Throwable t) {
			throw new RoutingException(t);
		}
	}

	public void setAttribute(Object target, String parameterName,
			String valueAsString) throws SecurityException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, NoSuchFieldException,
			InstantiationException, IOException {
		Class<? extends Object> clazz = target.getClass();
		Field field = Reflection.extractFieldFor(clazz,
				parameterName);
		if (field == null) {
			requestContext.log(String.format(
				"[WARN] Class %s doesn't have field with name '%s'.",
				clazz.getCanonicalName(), parameterName));
			return;
		}
		Object value = requestContext.convert(valueAsString, field.getType());
		Reflection.setAttribute(target, parameterName, value);
	}

	@SuppressWarnings("rawtypes")
	public Object createDataHandledObject(HandledParameter parameter)
			throws RoutingException {
		try {
			String canonicalName = parameter.getTargetClazz()
					.getCanonicalName();
			Class<? extends DataProvider> dataProviderClass = applicationContext
					.getRegisteredDataProviders().get(canonicalName);
			DataProvider dataProvider = (DataProvider<?>) dataProviderClass
					.newInstance();
			return dataProvider.newDataInstance(requestContext);
		} catch (Throwable t) {
			throw new RoutingException(t);
		}
	}
}
