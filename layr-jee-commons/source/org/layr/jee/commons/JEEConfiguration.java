package org.layr.jee.commons;

import java.util.Map;

import javax.servlet.ServletContext;

import org.layr.commons.Cache;
import org.layr.engine.components.IComponentFactory;

public class JEEConfiguration {
	
	private boolean isCacheEnabled;
	private String defaultResource;
	private ServletContext servletContext;
	private Map<String, IComponentFactory> registeredTagLibs;
	private Cache cache;

	public JEEConfiguration( ServletContext servletContext ) {
		setServletContext(servletContext);
		setCacheEnabled( readSystemProperty("cacheEnabled", "false") );
		setDefaultResource( "/home/" );
		setCache( new Cache() );
	}

	public String readSystemProperty( String propertyName, String defaultValue ){
		String propertyValue = System.getProperty("org.layr.config." + propertyName);
		if ( propertyValue == null || propertyValue.isEmpty() )
			propertyValue = defaultValue;
		return propertyValue;
	}

	public boolean isCacheEnabled() {
		return isCacheEnabled;
	}

	public void setCacheEnabled(String isCacheEnabled){
		setCacheEnabled( Boolean.valueOf(isCacheEnabled) );
	}

	public void setCacheEnabled(boolean isCacheEnabled) {
		this.isCacheEnabled = isCacheEnabled;
	}

	public String getDefaultResource() {
		return defaultResource;
	}

	public void setDefaultResource(String defaultResource) {
		this.defaultResource = defaultResource;
	}

	public ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void setRegisteredTagLibs(
			Map<String, IComponentFactory> registeredTagLibs) {
		this.registeredTagLibs = registeredTagLibs;
	}

	public Map<String, IComponentFactory> getRegisteredTagLibs() {
		return registeredTagLibs;
	}

	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}
}
