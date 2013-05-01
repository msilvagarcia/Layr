package layr.routing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static layr.commons.StringUtil.isEmpty;
import layr.engine.RequestContext;
import layr.engine.expressions.URLPattern;
import layr.routing.annotations.Route;

public class RouteMethod {

	RouteClass routeClass;
	Method targetMethod;
	List<RouteParameter> parameters;
	String pattern;
	Route routeAnnotation;

	HttpMethodAnnotationFactory httpMethodAnnotationFactory;
	Object lastReturnedValue;

	public RouteMethod( RouteClass routeClass, Method targetMethod ){
		this.routeClass = routeClass;
		this.targetMethod = targetMethod;
		this.httpMethodAnnotationFactory = new HttpMethodAnnotationFactory();
		this.parameters = new ArrayList<RouteParameter>();

		extractRouteMethodParameters();
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
			RouteParameterFactory.newInstance( annotation, targetClazz ) );
	}

	public Object invoke(Request request, Object instance) throws RoutingException {
		try {
			Object[] methodParameters = new Object[ parameters.size() ];
			short cursor = 0;
			for ( RouteParameter parameter : parameters )
				methodParameters[cursor++] = request.getValue( parameter );
			lastReturnedValue = targetMethod.invoke( instance, methodParameters );
			return lastReturnedValue;
		} catch (Exception e) {
			throw new RoutingException( "Can't to invoke the method " + targetMethod, e );
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
		Class<? extends Annotation> annotationClass = httpMethodAnnotationFactory
				.getHttpMethodAnnotationClass( requestContext.getRequestHttpMethod() );
		Annotation annotation = targetMethod.getAnnotation( annotationClass );
		return annotation != null;
	}

	String getRouteMethodPattern() {
		if ( pattern == null ) {
			Route route = getRouteAnnotation();
			String annotationPattern = isEmpty(route.value()) ? route.pattern() : route.value();
			pattern = (routeClass.rootPath + annotationPattern + "/?").replace( "//", "/" );
		}
		return pattern;
	}

	Route getRouteAnnotation() {
		if ( routeAnnotation == null )
			routeAnnotation = targetMethod.getAnnotation( Route.class );
		return routeAnnotation;
	}
}
