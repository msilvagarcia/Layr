package layr.routing.api;

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

public class HandledClass {

    Class<?> targetClass;
    List<HandledMethod> routes;
    List<HandledParameter> parameters;
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
			routes.add( createRouteMethod( method ) );
		return routes;
	}

	/**
	 * @param method
	 * @return
	 */
	public HandledMethod createRouteMethod(Method method) {
		return new HandledMethod( this, method );
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
	public List<HandledParameter> getParameters(){
		if ( parameters == null ) {
			parameters = new ArrayList<HandledParameter>();
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

	public PathHandledParameter createPathParameter(Field field, Annotation annotation) {
		String parameterName = oneOf( ((PathParameter)annotation).value(), field.getName() );
		return new PathHandledParameter( parameterName, field.getType() );
	}

	public QueryHandledParameter createQueryParameter(Field field, Annotation annotation) {
		String parameterName = oneOf( ((QueryParameter)annotation).value(), field.getName() );
		return new QueryHandledParameter( parameterName, field.getType() );
	}

	public TemplateHandledParameter createTemplateParameter(Field field, Annotation annotation) {
		String parameterName = oneOf( ((TemplateParameter)annotation).value(), field.getName() );
		return new TemplateHandledParameter( parameterName, field.getType() );
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
