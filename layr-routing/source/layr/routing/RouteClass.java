package layr.routing;

import java.lang.annotation.Annotation;
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
import layr.routing.annotations.TemplateParameter;
import layr.routing.annotations.WebResource;
import static layr.commons.StringUtil.*;

public class RouteClass {

    Class<?> targetClass;
    List<RouteMethod> routes;
    List<RouteParameter> parameters;
    String rootPath;

    public RouteClass(Class<?> targetClass) {
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
	public List<RouteMethod> extractMethodRoutes() {
		List<RouteMethod> routes = new ArrayList<RouteMethod>();
		for ( Method method : measureAvailableRoutes() )
			routes.add( createRouteMethod( method ) );
		return routes;
	}

	/**
	 * @param method
	 * @return
	 */
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

	/**
	 * @return
	 */
	public List<RouteParameter> getParameters(){
		if ( parameters == null ) {
			parameters = new ArrayList<RouteParameter>();
			for ( Field field : measureAvailableParameters() )
				createRouteParameter( field );
		}

		return parameters;
	}

	@SuppressWarnings("unchecked")
	public List<Field> measureAvailableParameters() {
		return Reflection.extractAnnotatedFieldsFor( targetClass,
			TemplateParameter.class,
			PathParameter.class,
			QueryParameter.class );
	}

	public void createRouteParameter(Field field) {
		for ( Annotation annotation : field.getAnnotations()){
			if ( PathParameter.class.isInstance( annotation ) )
				parameters.add( createPathParameter( field, annotation ));
			else if ( QueryParameter.class.isInstance( annotation ) )
				parameters.add( createQueryParameter( field, annotation ));
			else if ( TemplateParameter.class.isInstance( annotation ) )
				parameters.add( createTemplateParameter( field, annotation ));
		}
	}

	public PathRouteParameter createPathParameter(Field field, Annotation annotation) {
		String parameterName = oneOf( ((PathParameter)annotation).value(), field.getName() );
		return new PathRouteParameter( parameterName, field.getType() );
	}

	public QueryRouteParameter createQueryParameter(Field field, Annotation annotation) {
		String parameterName = oneOf( ((QueryParameter)annotation).value(), field.getName() );
		return new QueryRouteParameter( parameterName, field.getType() );
	}

	public TemplateRouteParameter createTemplateParameter(Field field, Annotation annotation) {
		String parameterName = oneOf( ((TemplateParameter)annotation).value(), field.getName() );
		return new TemplateRouteParameter( parameterName, field.getType() );
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
	public List<RouteMethod> getRouteMethods() {
		return routes;
    }
}
