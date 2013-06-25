package layr.routing.lifecycle;

import java.lang.annotation.Annotation;

import layr.api.Data;
import layr.api.PathParameter;
import layr.api.QueryParameter;
import layr.api.QueryParameters;

public final class HandledParameterFactory {

	public static HandledParameter newInstance( Annotation annotation, Class<?> targetClazz ){
		if ( PathParameter.class.isInstance( annotation ) )
			return new PathHandledParameter( ((PathParameter)annotation).value(), targetClazz );
		if ( QueryParameter.class.isInstance( annotation ) )
			return new QueryHandledParameter( ((QueryParameter)annotation).value(), targetClazz );
		if ( Data.class.isInstance( annotation ) )
			return new DataHandledParameter( targetClazz );
		if ( QueryParameters.class.isInstance(annotation) )
			return new QueryHandledParameters( targetClazz );
		return new HandledParameter(null, targetClazz);
	}

}