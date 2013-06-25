package layr.api;

import layr.exceptions.DataProviderException;

public interface DataProvider<T> {

	T newDataInstance( RequestContext requestContext ) throws DataProviderException;

}
