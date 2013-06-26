package layr.jee;

import java.util.Set;

import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import layr.api.ContentType;
import layr.api.Handler;
import layr.api.TagLib;
import layr.api.WebResource;
import layr.routing.lifecycle.ApplicationContext;
import layr.routing.lifecycle.HandledClass;
import layr.routing.lifecycle.HandledMethod;

@HandlesTypes({
	TagLib.class,
	WebResource.class,
	Handler.class,
	ContentType.class,
	Stateless.class,
	Stateful.class,
	Singleton.class
})
public class Initialization implements javax.servlet.ServletContainerInitializer {

	@Override
	public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
		try {
			servletContext.log("Starting Layr initialization.");
			JEERoutingBootstrap bootstrap = new JEERoutingBootstrap();
			ApplicationContext applicationContext = bootstrap.configure(classes);
			servletContext.setAttribute(JEEConfiguration.class.getCanonicalName(), applicationContext);
			logFoundResources(servletContext, bootstrap);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	private void logFoundResources(ServletContext servletContext, JEERoutingBootstrap bootstrap) {
		for ( HandledClass clazz : bootstrap.getRegisteredWebResources() ){
			servletContext.log("WebResource: " + clazz.getTargetClass());
			for ( HandledMethod method : clazz.getRouteMethods() )
				servletContext.log(" - " + method.getHttpMethod() + " " + method.getRouteMethodPattern());
			servletContext.log("");
		}
	}
}
