package layr.routing;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import layr.routing.annotations.DELETE;
import layr.routing.annotations.GET;
import layr.routing.annotations.POST;
import layr.routing.annotations.PUT;

public class HttpMethodAnnotationFactory {
	
	Map<String, Class<? extends Annotation>> registeredHttpMethodsAnnotations;

	@SuppressWarnings("unchecked")
	public HttpMethodAnnotationFactory() {
		registeredHttpMethodsAnnotations = new HashMap<String, Class<? extends Annotation>>();
		registerAnnotations( GET.class, POST.class, PUT.class, DELETE.class );
	}

	public void registerAnnotations( Class<? extends Annotation>...annotations ) {
		for ( Class<? extends Annotation> annotation : annotations )
			registerAnnotation( annotation );
	}
	
	public void registerAnnotation( Class<? extends Annotation> annotation ) {
		registeredHttpMethodsAnnotations.put( annotation.getSimpleName(), annotation );
	}

	public Class<? extends Annotation> getHttpMethodAnnotationClass( String httpMethod ) {
		return registeredHttpMethodsAnnotations.get( httpMethod );
	}
}
