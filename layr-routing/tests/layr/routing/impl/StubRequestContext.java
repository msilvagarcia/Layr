package layr.routing.impl;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import layr.engine.AbstractRequestContext;
import layr.engine.Cache;
import layr.engine.components.ComponentFactory;
import layr.org.codehaus.jackson.ConversionException;
import layr.org.codehaus.jackson.ConverterFactory;

public class StubRequestContext extends AbstractRequestContext {
	
	StringWriter writer;
	String requestURI;
	Map<String, String> requestParameters;
	String requestHttpMethod;
	String redirectedURL;
	int statusCode;
	ConverterFactory converter;

	public StubRequestContext() {
		writer = new StringWriter();
		converter = new ConverterFactory();
		populateWithDefaultTagLibs();
		createCache();
	}

	public void createCache() {
		Cache cache = new Cache();
		setCache(cache);
	}

	public void populateWithDefaultTagLibs() {
		HashMap<String, ComponentFactory> registeredTagLibs = new HashMap<String, ComponentFactory>();
		setRegisteredTagLibs( registeredTagLibs );
	}

	public String getBufferedWroteContentToOutput(){
		return writer.getBuffer().toString();
	}

	@Override
	public void setContentType(String contentType) {
		
	}

	@Override
	public void setCharacterEncoding(String encoding) {}

	@Override
	public Writer getWriter() {
		return writer;
	}

	@Override
	public String getRequestURI() {
		return returnRequestURIOrDefaultRequest( requestURI );
	}

	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}

	@Override
	public Map<String, String> getRequestParameters() {
		return requestParameters;
	}

	public void setRequestParameters(Map<String, String> requestParameters) {
		this.requestParameters = requestParameters;
	}

	@Override
	public Object convert(String value, Class<?> targetClass) throws IOException {
		try {
			return converter.convert( value, targetClass );
		} catch (ConversionException e) {
			throw new IOException( e );
		}
	}

	@Override
	public String getApplicationRootPath() {
		return "/";
	}

	@Override
	public String getRequestHttpMethod() {
		return requestHttpMethod;
	}
	
	public void setRequestHttpMethod(String requestHttpMethod) {
		this.requestHttpMethod = requestHttpMethod;
	}

	@Override
	public void redirectTo(String redirectTo) {
		this.redirectedURL = redirectTo;
	}
	
	public String getRedirectedURL() {
		return redirectedURL;
	}

	@Override
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
}