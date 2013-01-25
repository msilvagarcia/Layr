package org.layr.engine;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import javax.servlet.ServletException;

import org.layr.engine.components.IComponent;
import org.layr.engine.components.IComponentFactory;

public interface IRequestContext {

	/**
	 * @param paramName
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws FileUploadException
	 */
	public abstract Object getParameter(String paramName)
			throws ServletException, IOException;

	public abstract void registerNamespace(String namespace,
			IComponentFactory factory);

	public abstract boolean isRegisteredNamespace(String namespace);

	public abstract IRequestContext put(String name, Object property);

	public abstract Object get(String name);

	public abstract String getRelativePath();

	public abstract String getNextId();

	public abstract String getWebResourceRootPath();

	public abstract void setWebResourceRootPath(String path);

	public abstract void setCharacterEncoding(String encoding);

	public abstract void setContentType(String contentType);

	public abstract void log(String text);

	public abstract IComponentFactory getComponentFactory(String uri);

	public abstract InputStream getResourceAsStream(String resourceName) throws IOException;

	public abstract Writer getWriter();

	public abstract String getApplicationRootPath();
	
	public abstract void setApplicationRootPath( String contextPath );

	public abstract void setRelativePath(String relativePath);

	void putInCacheTheCompiledResource(String templateName, IComponent application);

	IComponent getResourceFromCache(String templateName);

}