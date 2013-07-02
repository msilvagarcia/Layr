package layr.routing.lifecycle;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import layr.api.ApplicationContext;
import layr.api.DataProvider;
import layr.api.InputConverter;
import layr.api.RequestContext;
import layr.commons.Reflection;
import layr.engine.expressions.URLPattern;
import layr.exceptions.RoutingException;
import layr.routing.converter.DefaultInputConverter;

public class Request {

	ApplicationContext applicationContext;
	RequestContext requestContext;
	String routePattern;

	Map<String, String> pathParameters;
	Map<String, String> requestParameters;
	InputConverter inputConverter;

	public Request(ApplicationContext applicationContext,
			RequestContext requestContext, String routePattern) throws IOException {
		this.applicationContext = applicationContext;
		this.requestContext = requestContext;
		this.routePattern = routePattern;
		memorizeParameters();
		memorizeInputConverter();
	}

	void memorizeParameters() {
		pathParameters = extractPathParameters();
		requestParameters = requestContext.getRequestParameters();
	}

	void memorizeInputConverter() throws IOException {
		try {
			Class<? extends InputConverter> clazz = null;
			String contentType = requestContext.getContentType();
			if (contentType != null)
				clazz = applicationContext.getRegisteredInputConverters().get(contentType);
			if (clazz == null)
				clazz = DefaultInputConverter.class;
			inputConverter = clazz.newInstance();
		} catch (Exception e) {
			throw new IOException(e);
		}
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
			return inputConverter.convert((String) value,
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
		return createObjectFromBody(parameter);
	}

	public Object createObjectFromBody(HandledParameter parameter)
			throws RoutingException {
		try {
			InputStream inputStream = requestContext.getRequestInputStream();
			Object convertedObject = inputConverter.convert(inputStream, parameter.getTargetClazz());
			return convertedObject;
		} catch (Throwable t) {
			throw new RoutingException(t);
		}
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
		Field field = Reflection.extractFieldFor(clazz, parameterName);
		if (field == null) {
			requestContext.log(
				String.format("[WARN] Class %s doesn't have field with name '%s'.",
					clazz.getCanonicalName(), parameterName));
			return;
		}
		Object value = inputConverter.convert(valueAsString, field.getType());
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
			return dataProvider.newDataInstance(applicationContext, requestContext);
		} catch (Throwable t) {
			throw new RoutingException(t);
		}
	}
}
