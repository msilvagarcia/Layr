package layr.core.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import layr.core.ApplicationContext;
import layr.core.LayrFactory;
import layr.core.expressions.ComplexExpressionEvaluator;


@WebListener
public class LayrUnitializer implements ServletContextListener {
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		servletContext.log("[Layr] Removing Layr Caches...");

		ApplicationContext applicationContext = LayrFactory.getOrCreateApplicationContext(servletContext);
		applicationContext.clearCache();
		ComplexExpressionEvaluator.clearCache();
		LayrFactory.destroyApplicationContext(applicationContext);

		System.gc();
		servletContext.log("[Layr] Released memory will be available next time Garbage Collection back to the scene.");
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {}

}
