package layr.core.servlet;

import java.io.IOException;
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
import javax.xml.parsers.ParserConfigurationException;

import layr.core.ApplicationContext;
import layr.core.Configuration;
import layr.core.LayrFactory;
import layr.core.commons.EnterpriseJavaBeans;
import layr.core.components.DefaultComponentFactory;
import layr.core.components.IComponentFactory;
import layr.core.components.TagLib;

import org.xml.sax.SAXException;

@HandlesTypes({
	Stateless.class,
	Stateful.class,
	Singleton.class,
	TagLib.class
})
public class LayrInitializer implements ServletContainerInitializer {

	private Map<String, IComponentFactory> registeredTagLibs;
	private EnterpriseJavaBeans ejbManager;
	private ApplicationContext applicationContext;

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContainerInitializer#onStartup(java.util.Set, javax.servlet.ServletContext)
	 */
	@Override
	public void onStartup(Set<Class<?>> classes, ServletContext servletContext)
			throws ServletException {

		try {
			initializeApplicationContext(servletContext);
			readConfiguration();
		
			for (Class<?> clazz : classes) {
				handleClass(clazz);
			}

		} catch (Throwable e) {
			throw new ServletException("Can't initialize the Application Context.", e);
		}

		reportLayrInitializationStatus(servletContext);
	}

	public void handleClass(Class<?> clazz) throws InstantiationException,
			IllegalAccessException {
		tryToRegisterATag(clazz);
		ejbManager.seekForJNDIEJBViewsfor(clazz);
	}

	public void reportLayrInitializationStatus(ServletContext servletContext) {
		servletContext.log("[Layr] Configuration Loaded!");
		servletContext.log("[Layr] Is Request Cache Enabled: " + applicationContext.getLayrConfiguration().isCacheEnabled());
		servletContext.log("[Layr] Is Equation Support Enabled (it reduce 25% performance): " + (!applicationContext.getLayrConfiguration().isEquationsDisabled()));
		servletContext.log("[Layr] Default Welcome Resource mapped to: " + applicationContext.getLayrConfiguration().getDefaultResource());
		servletContext.log("[Layr] Application Context initialized.");
	}

	/**
	 * Initialize Layr Application Context and configure it for further use.
	 * @param servletContext
	 */
	public void initializeApplicationContext(ServletContext servletContext) throws NamingException {
		applicationContext = LayrFactory.getOrCreateApplicationContext(servletContext);
		applicationContext.initializeApplicationContexts();
		
		ejbManager = new EnterpriseJavaBeans();
		registeredTagLibs = new HashMap<String, IComponentFactory>();

		applicationContext.setEjbManager(ejbManager);
		applicationContext.setRegisteredTagLibs(registeredTagLibs);
	}

	/**
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public void readConfiguration() throws IOException, ParserConfigurationException, SAXException {
		applicationContext.setLayrConfiguration(new Configuration());
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
		IComponentFactory factory = (IComponentFactory)clazz.newInstance();

		if (DefaultComponentFactory.class.isInstance(factory))
			((DefaultComponentFactory)factory).setRootDir(
				clazz.getPackage().getName().replace(".", "/"));

		getRegisteredTagLibs().put(namespace, factory);
	}

	public Map<String, IComponentFactory> getRegisteredTagLibs() {
		return registeredTagLibs;
	}

	public void setRegisteredTagLibs(Map<String, IComponentFactory> registeredTagLibs) {
		this.registeredTagLibs = registeredTagLibs;
	}

	public EnterpriseJavaBeans getEjbManager() {
		return ejbManager;
	}

	public void setEjbManager(EnterpriseJavaBeans ejbManager) {
		this.ejbManager = ejbManager;
	}

}
