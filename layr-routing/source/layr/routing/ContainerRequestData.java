package layr.routing;

/**
 * Represents the request coming from the container. It will
 * send the requested data to the <b>Configuration.createContext</b>
 * the information need to create an <b>RequestContext</b> object. 
 *
 * @param <Rq> Container object that represents the request
 * @param <Rp> Container object that represents the response
 */
public interface ContainerRequestData<Rq,Rp> {

	Rq getRequest();

	Rp getResponse();

}
