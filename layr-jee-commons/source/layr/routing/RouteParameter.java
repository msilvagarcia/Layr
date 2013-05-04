package layr.routing;

import java.lang.annotation.Annotation;

import layr.routing.annotations.PathParameter;
import layr.routing.annotations.QueryParameter;

public class RouteParameter {
	String name;
	Class<?> targetClazz;

	public RouteParameter(String name, Class<?> targetClazz) {
		this.name = name;
		this.targetClazz = targetClazz;
	}
}

final class PathRouteParameter extends RouteParameter {
	public PathRouteParameter(String name, Class<?> targetClazz) {
		super( name, targetClazz );
	}
}

final class QueryRouteParameter extends RouteParameter {
	public QueryRouteParameter(String name, Class<?> targetClazz) {
		super( name, targetClazz );
	}
}

final class TemplateRouteParameter extends RouteParameter {
	public TemplateRouteParameter(String name, Class<?> targetClazz) {
		super( name, targetClazz );
	}
}

final class RouteParameterFactory {

	public static RouteParameter newInstance( Annotation annotation, Class<?> targetClazz ){
		if ( PathParameter.class.isInstance( annotation ) )
			return new PathRouteParameter( ((PathParameter)annotation).value(), targetClazz );
		if ( QueryParameter.class.isInstance( annotation ) )
			return new QueryRouteParameter( ((QueryParameter)annotation).value(), targetClazz );
		return null;
	}

}