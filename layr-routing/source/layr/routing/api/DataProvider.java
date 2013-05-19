package layr.routing.api;

import layr.engine.RequestContext;
import layr.routing.exceptions.DataProviderException;

public interface DataProvider<T> {

	T newDataInstance( ApplicationContext applicationContext, RequestContext requestContext ) throws DataProviderException;

}
