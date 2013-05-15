package layr.routing.api;

import java.lang.annotation.Annotation;

import layr.routing.annotations.PathParameter;
import layr.routing.annotations.QueryParameter;

public final class RouteParameterFactory {

	public static RouteParameter newInstance( Annotation annotation, Class<?> targetClazz ){
		if ( PathParameter.class.isInstance( annotation ) )
			return new PathRouteParameter( ((PathParameter)annotation).value(), targetClazz );
		if ( QueryParameter.class.isInstance( annotation ) )
			return new QueryRouteParameter( ((QueryParameter)annotation).value(), targetClazz );
		return null;
	}

}