package layr.api;

import layr.exceptions.DataProviderException;

public interface DataProvider<T> {

	T newDataInstance( ApplicationContext applicationContext,
			RequestContext requestContext ) throws DataProviderException;

}
