package layr.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;

/**
 * Feeds the template engine and the request life cycle with both container's
 * request and response data.
 */
public interface RequestContext {
	
	public abstract void put(String name, Object property);
	
	public abstract Object get(String name);

	public abstract void registerNamespace(String namespace, ComponentFactory factory);

	public abstract boolean isRegisteredNamespace(String namespace);

	public abstract Cache getCache();

	public abstract ComponentFactory getComponentFactory(String uri);

	public abstract InputStream getResourceAsStream(String resourceName) throws IOException;

	public abstract Writer getWriter() throws IOException;

	public abstract void redirectTo(String redirectTo) throws IOException;

	public abstract void setStatusCode(int statusCode);

	public abstract void setCharacterEncoding(String encoding) throws UnsupportedEncodingException;

	public abstract void setContentType(String contentType);

	public abstract String getRequestHttpMethod();

	public abstract String getRequestURI();
	
	public abstract String getApplicationRootPath();

	public abstract Map<String, String> getRequestParameters();

	public abstract void log(String text);

	public abstract void cacheCompiledResource(String templateName, Component application);

	public abstract Component getResourceFromCache(String templateName);

	public abstract String getDefaultResource();

	public abstract boolean isAsyncRequest();
	
	public abstract InputStream getRequestInputStream() throws IOException;
	
	public abstract OutputStream getResponseOutputStream() throws IOException;

	public abstract String getContentType();

}