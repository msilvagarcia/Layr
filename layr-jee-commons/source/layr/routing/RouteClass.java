package layr.routing;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import layr.commons.Reflection;
import layr.engine.RequestContext;
import layr.engine.expressions.URLPattern;
import layr.routing.annotations.PathParameter;
import layr.routing.annotations.QueryParameter;
import layr.routing.annotations.Route;
import layr.routing.annotations.WebResource;

public class RouteClass {

    static final String AVAILABLES_ROUTES = RouteClass.class.getCanonicalName() + ".AVAILABLES_ROUTES";

    Class<?> targetClass;
    List<RouteMethod> routes;
    List<RouteParameter> parameters;
    String rootPath;

    public RouteClass(Class<?> targetClass) {
    	this.targetClass = targetClass;
    	this.rootPath = extractRootPath();
	}

	public String extractRootPath() {
		WebResource webResource = targetClass.getAnnotation( WebResource.class );
		String rootPath = webResource.value();
		return ("/" + rootPath + "/").replace("//", "/");
	}

	/**
	 * @return
	 */
	public List<RouteMethod> getRouteMethods() {
		if ( routes == null ) {
			routes = new ArrayList<RouteMethod>();
			for ( Method method : measureAvailableRoutes() )
				routes.add( createRouteMethod( method ) );
		}
		return routes;
    }

	public RouteMethod createRouteMethod(Method method) {
		return new RouteMethod( this, method );
	}

	/**
	 * Measure which route methods are available from target instance
	 * @return 
	 */
	public List<Method> measureAvailableRoutes() {
		return Reflection.extractAnnotatedMethodsFor(targetClass, Route.class);
	}
	
	public List<RouteParameter> getParameters(){
		if ( parameters == null ) {
			parameters = new ArrayList<RouteParameter>();
			for ( Field field : measureAvailableParameters() )
				parameters.add( createRouteParameter( field ) );
		}
		
		return parameters;
	}

	@SuppressWarnings("unchecked")
	public List<Field> measureAvailableParameters() {
		return Reflection.extractAnnotatedFieldsFor( targetClass, PathParameter.class, QueryParameter.class );
	}

	public RouteParameter createRouteParameter(Field field) {
		return RouteParameterFactory.newInstance( field, targetClass );
	}

	public boolean matchesTheRequestURI(RequestContext requestContext) {
		String methodUrlPattern = new URLPattern().parseMethodUrlPatternToRegExp( rootPath ) + ".*";
		return requestContext.getRequestURI().matches( methodUrlPattern );
	}
}
