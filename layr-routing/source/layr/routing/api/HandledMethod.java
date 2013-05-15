package layr.routing.api;

import static layr.commons.StringUtil.isEmpty;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import layr.engine.RequestContext;
import layr.engine.expressions.URLPattern;
import layr.routing.annotations.DELETE;
import layr.routing.annotations.POST;
import layr.routing.annotations.PUT;
import layr.routing.annotations.Route;

public class HandledMethod {

	HandledClass routeClass;
	Method targetMethod;
	List<HandledParameter> parameters;
	String pattern;
	Route routeAnnotation;

	Object lastReturnedValue;
	String httpMethod;

	public HandledMethod( HandledClass routeClass, Method targetMethod ){
		this.routeClass = routeClass;
		this.targetMethod = targetMethod;
		this.parameters = new ArrayList<HandledParameter>();

		extractRouteHttpMethod();
		extractRouteMethodParameters();
	}
	
	public void extractRouteHttpMethod() {
		if ( targetMethod.isAnnotationPresent( POST.class ) )
			this.httpMethod = "POST";
		else if ( targetMethod.isAnnotationPresent( PUT.class ) )
			this.httpMethod = "PUT";
		else if ( targetMethod.isAnnotationPresent( DELETE.class ) )
			this.httpMethod = "DELETE";
		else
			this.httpMethod = "GET";
	}

	public void extractRouteMethodParameters() {
		Class<?>[] parameterTypes = targetMethod.getParameterTypes();
        Annotation[][] parameterAnnotations = targetMethod.getParameterAnnotations();

        short cursor = 0;
        for ( Annotation[] annotations : parameterAnnotations ) {
        	Annotation annotation = annotations[0];
        	Class<?> clazz = parameterTypes[cursor];
        	memorizeParameterFromAnnotation( annotation, clazz );
        	cursor++;
        }
	}

	void memorizeParameterFromAnnotation(Annotation annotation, Class<?> targetClazz) {
		this.parameters.add(
			HandledParameterFactory.newInstance( annotation, targetClazz ) );
	}

	public Object invoke(Request request, Object instance) throws Throwable {
		try {
			Object[] methodParameters = new Object[ parameters.size() ];
			short cursor = 0;
			for ( HandledParameter parameter : parameters )
				methodParameters[cursor++] = request.getValue( parameter );
			lastReturnedValue = targetMethod.invoke( instance, methodParameters );
			return lastReturnedValue;
		} catch ( InvocationTargetException e ) {
			throw e.getTargetException();
		}
	}

	public Map<String, String> extractPathParameters(RequestContext requestContext) {
		return new URLPattern().extractMethodPlaceHoldersValueFromURL(
				getRouteMethodPattern(),
				requestContext.getRequestURI() );
	}

	public boolean matchesTheRequest( RequestContext requestContext ) {
		return matchesTheRequestURI( requestContext )
			&& matchesTheHTTPMethod( requestContext );
	}

	public boolean matchesTheRequestURI(RequestContext requestContext) {
		String fullPathPattern = getRouteMethodPattern();
		String methodUrlPattern = new URLPattern().parseMethodUrlPatternToRegExp( fullPathPattern );
		return requestContext.getRequestURI().matches( methodUrlPattern );
	}

	public boolean matchesTheHTTPMethod(RequestContext requestContext) {
		return requestContext.getRequestHttpMethod().equals( httpMethod );
	}

	public String getRouteMethodPattern() {
		if ( pattern == null ) {
			Route route = getRouteAnnotation();
			String annotationPattern = isEmpty(route.value()) ? route.pattern() : route.value();
			pattern = (routeClass.rootPath + annotationPattern + "/?").replace( "//", "/" );
		}
		return pattern;
	}

	public Route getRouteAnnotation() {
		if ( routeAnnotation == null )
			routeAnnotation = targetMethod.getAnnotation( Route.class );
		return routeAnnotation;
	}
	
	public HandledClass getRouteClass() {
		return routeClass;
	}
	
	public Object getLastReturnedValue() {
		return lastReturnedValue;
	}
}
