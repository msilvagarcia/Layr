package layr.routing.lifecycle;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import layr.api.DELETE;
import layr.api.GET;
import layr.api.POST;
import layr.api.PUT;
import layr.api.RequestContext;
import layr.api.WebResource;
import layr.commons.Reflection;
import layr.engine.expressions.URLPattern;

/**
 * Extract all Layr needed information from an class that
 * should to be used during requests. Developers should not
 * "handle" classes during request time, but during deploy time
 * instead. This approach will grant that requests are lightweight
 * enough for a fast response.
 */
public class HandledClass {

    Class<?> targetClass;
    List<HandledMethod> routes;
    String rootPath;

    public HandledClass(Class<?> targetClass) {
    	this.targetClass = targetClass;
    	this.rootPath = extractRootPath();
    	this.routes = extractMethodRoutes();
	}

	/**
	 * @return
	 */
	public String extractRootPath() {
		WebResource webResource = targetClass.getAnnotation( WebResource.class );
		String rootPath = webResource.value();
		return ("/" + rootPath + "/").replace("//", "/");
	}

	/**
	 * @return
	 */
	public List<HandledMethod> extractMethodRoutes() {
		List<HandledMethod> routes = new ArrayList<HandledMethod>();
		for ( Method method : measureAvailableRoutes() )
			for ( Annotation annotation : method.getAnnotations() ) {
				HandledMethod routeMethod = createRouteMethod( method, annotation );
				if ( routeMethod != null )
					routes.add( routeMethod );
			}
		return routes;
	}

	/**
	 * @param method
	 * @return
	 */
	public HandledMethod createRouteMethod(Method method, Annotation httpMethodAnnotation) {
		if (httpMethodAnnotation instanceof POST)
			return new HandledMethod( this, method, "POST", ((POST) httpMethodAnnotation).value() );
		else if (httpMethodAnnotation instanceof PUT)
			return new HandledMethod( this, method, "PUT", ((PUT) httpMethodAnnotation).value() );
		else if (httpMethodAnnotation instanceof DELETE)
			return new HandledMethod( this, method, "DELETE", ((DELETE) httpMethodAnnotation).value() );
		else if (httpMethodAnnotation instanceof GET)
			return new HandledMethod( this, method, "GET", ((GET) httpMethodAnnotation).value() );
		return null;
	}

	/**
	 * Measure which route methods are available from target instance
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	public List<Method> measureAvailableRoutes() {
		return Reflection.extractAnnotatedMethodsFor(
				targetClass, GET.class, POST.class, DELETE.class, PUT.class);
	}

	public boolean matchesTheRequestURI(RequestContext requestContext) {
		String methodUrlPattern = new URLPattern().parseMethodUrlPatternToRegExp( rootPath ) + ".*";
		String requestURI = requestContext.getRequestURI();
		return requestURI.matches( methodUrlPattern );
	}

	/**
	 * @return
	 */
	public Class<?> getTargetClass() {
		return targetClass;
	}

	/**
	 * @return
	 */
	public List<HandledMethod> getRouteMethods() {
		return routes;
    }
}
