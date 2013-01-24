package org.layr.jee.routing.business;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import org.layr.commons.StringUtil;
import org.layr.engine.AbstractRequestContext;
import org.layr.engine.components.DefaultComponentFactory;
import org.layr.engine.components.IComponentFactory;
import org.layr.engine.components.TagLib;
import org.layr.jee.commons.EnterpriseJavaBeans;


@HandlesTypes({
	TagLib.class,
	WebResource.class,
	Stateless.class,
	Stateful.class,
	Singleton.class})
public class PluginInitializer implements ServletContainerInitializer {

	private Map<String, Object> registeredWebResources;
	private HashMap<String, IComponentFactory> registeredTagLibs;
	private EnterpriseJavaBeans ejbManager;

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContainerInitializer#onStartup(java.util.Set, javax.servlet.ServletContext)
	 */
	@Override
	public void onStartup(Set<Class<?>> classes, ServletContext servletContext)
			throws ServletException {
		try {
			initializeApplicationContext(servletContext);
			for (Class<?> clazz : classes){
				tryToRegisterAWebResource(clazz);
				tryToRegisterATag(clazz);
				ejbManager.seekForJNDIEJBViewsfor(clazz);
			}
		} catch (Throwable e) {
			throw new ServletException("Can't initialize the Application Context.", e);
		}
		reportLayrInitializationStatus(servletContext);
	}

	/**
	 * @param servletContext
	 */
	public void reportLayrInitializationStatus(ServletContext servletContext) {
		servletContext.log("[Layr] JEE Business Routing Plugin loaded");
	}

	/**
	 * Initialize Layr Application Context and configure it for further use.
	 * @param servletContext
	 */
	public void initializeApplicationContext(ServletContext servletContext) throws NamingException {
		ejbManager = new EnterpriseJavaBeans();
		registeredWebResources = new HashMap<String, Object>();
		registeredTagLibs = initializeTagLibs();

		JEEBusinessRoutingConfiguration configuration = new JEEBusinessRoutingConfiguration(servletContext);
		configuration.setWebResources(registeredWebResources);
		configuration.setRegisteredTagLibs(registeredTagLibs);
		configuration.setEjbManager(ejbManager);
		configuration.initializeApplicationContexts();

		servletContext.setAttribute(JEEBusinessRoutingConfiguration.class.getCanonicalName(), configuration);
	}
	
	public HashMap<String, IComponentFactory> initializeTagLibs(){
		HashMap<String, IComponentFactory> registeredTagLibs = new HashMap<String, IComponentFactory>();
		AbstractRequestContext.populateWithDefaultTagLibs(registeredTagLibs);
		return registeredTagLibs;
	}

	public void tryToRegisterATag(Class<?> clazz)
			throws InstantiationException, IllegalAccessException
	{
		TagLib annotation = clazz.getAnnotation(TagLib.class);
		if (annotation == null)
			return;

		String namespace = annotation.value();
		IComponentFactory factory = (IComponentFactory)clazz.newInstance();

		if (DefaultComponentFactory.class.isInstance(factory))
			((DefaultComponentFactory)factory).setRootDir(
				clazz.getPackage().getName().replace(".", "/"));

		registeredTagLibs.put(namespace, factory);
	}

	/**
	 * Try to Register a WebResource based on class annotation.
	 * @param clazz
	 */
	public void tryToRegisterAWebResource(Class<?> clazz) {
		WebResource annotation = clazz.getAnnotation(WebResource.class);
		if (annotation == null)
			return;

		String url = annotation.value();
		if (StringUtil.isEmpty(url))
			url = annotation.rootURL();

		if (StringUtil.isEmpty(url))
			return;
		
		getRegisteredWebResources().put(url, clazz);
	}

	public Map<String, Object> getRegisteredWebResources() {
		return registeredWebResources;
	}

	public void setRegisteredWebResources(Map<String, Object> registeredWebResources) {
		this.registeredWebResources = registeredWebResources;
	}

}
