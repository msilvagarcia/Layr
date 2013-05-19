package layr.routing.api;

import layr.engine.RequestContext;

public interface DataProvider<T> {

	T newDataInstance( ApplicationContext applicationContext, RequestContext requestContext );

}
