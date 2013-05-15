package layr.routing.api;

import java.lang.annotation.Annotation;

import layr.routing.annotations.PathParameter;
import layr.routing.annotations.QueryParameter;

public final class HandledParameterFactory {

	public static HandledParameter newInstance( Annotation annotation, Class<?> targetClazz ){
		if ( PathParameter.class.isInstance( annotation ) )
			return new PathHandledParameter( ((PathParameter)annotation).value(), targetClazz );
		if ( QueryParameter.class.isInstance( annotation ) )
			return new QueryHandledParameter( ((QueryParameter)annotation).value(), targetClazz );
		return null;
	}

}