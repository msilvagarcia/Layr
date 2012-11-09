package org.layr.jee.routing.business;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class PluginUnitializer implements ServletContextListener {
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		servletContext.log("[Layr] Cleaning Some Caches...");

		JEEBusinessRoutingConfiguration configuration = (JEEBusinessRoutingConfiguration)servletContext.getAttribute(
				JEEBusinessRoutingConfiguration.class.getCanonicalName() );
		if ( configuration != null && configuration.getCache() != null )
			configuration.getCache().clearCache();

		System.gc();
		servletContext.log("[Layr] Released memory will be available next time Garbage Collection back to the scene.");
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {}

}
