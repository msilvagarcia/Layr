package org.layr.jee.routing.natural;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import layr.engine.AbstractRequestContext;
import layr.engine.components.ComponentFactory;
import layr.engine.components.DefaultComponentFactory;
import layr.engine.components.TagLib;
import layr.routing.Configuration;


@HandlesTypes(TagLib.class)
public class PluginInitializer implements ServletContainerInitializer {

	private Map<String, ComponentFactory> registeredTagLibs;
	private Configuration jeeConfiguration;

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContainerInitializer#onStartup(java.util.Set, javax.servlet.ServletContext)
	 */
	@Override
	public void onStartup(Set<Class<?>> classes, ServletContext servletContext)
			throws ServletException {
		try {
			initializeConfiguration(servletContext);
			for (Class<?> clazz : classes)
				handleClass(clazz);
			reportLayrInitializationStatus(servletContext);
		} catch (Throwable e) {
			throw new ServletException("Can't initialize the Application Context.", e);
		}
	}

	/**
	 * @param clazz
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void handleClass(Class<?> clazz) throws InstantiationException,
			IllegalAccessException {
		tryToRegisterATag(clazz);
	}

	/**
	 * @param servletContext
	 */
	public void reportLayrInitializationStatus(ServletContext servletContext) {
		servletContext.log("[Layr] JEE Natural Routing Plugin loaded");
	}

	/**
	 * Initialize Layr Natural Routing Configuration and configure it for further use.
	 * @param servletContext
	 */
	public void initializeConfiguration(ServletContext servletContext) throws NamingException {
		registeredTagLibs = initializeTagLibs();
		setJeeConfiguration(new Configuration(servletContext));
		getJeeConfiguration().setRegisteredTagLibs(registeredTagLibs);
		servletContext.setAttribute(Configuration.class.getCanonicalName(), getJeeConfiguration());
	}
	
	/**
	 * @return
	 */
	public Map<String, ComponentFactory> initializeTagLibs(){
		HashMap<String, ComponentFactory> registeredTagLibs = new HashMap<String, ComponentFactory>();
		AbstractRequestContext.populateWithDefaultTagLibs(registeredTagLibs);
		return registeredTagLibs;
	}

	/**
	 * Try to Register a TagLib based on class annotation.
	 * 
	 * @param clazz
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void tryToRegisterATag(Class<?> clazz)
			throws InstantiationException, IllegalAccessException
	{
		TagLib annotation = clazz.getAnnotation(TagLib.class);
		if (annotation == null)
			return;

		String namespace = annotation.value();
		ComponentFactory factory = (ComponentFactory)clazz.newInstance();
		defineRootDirIfIsADefaultComponentFactory(clazz, factory);
		getRegisteredTagLibs().put(namespace, factory);
	}

	/**
	 * @param clazz
	 * @param factory
	 */
	public void defineRootDirIfIsADefaultComponentFactory(Class<?> clazz, ComponentFactory factory) {
		if (DefaultComponentFactory.class.isInstance(factory))
			((DefaultComponentFactory)factory).setRootDir(
				clazz.getPackage().getName().replace(".", "/"));
	}

	/**
	 * @return
	 */
	public Map<String, ComponentFactory> getRegisteredTagLibs() {
		return registeredTagLibs;
	}

	/**
	 * @param registeredTagLibs
	 */
	public void setRegisteredTagLibs(Map<String, ComponentFactory> registeredTagLibs) {
		this.registeredTagLibs = registeredTagLibs;
	}

	/**
	 * @return
	 */
	public Configuration getJeeConfiguration() {
		return jeeConfiguration;
	}

	/**
	 * @param jeeConfiguration
	 */
	public void setJeeConfiguration(Configuration jeeConfiguration) {
		this.jeeConfiguration = jeeConfiguration;
	}

}
