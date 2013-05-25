package layr.routing.lifecycle;

import layr.api.RequestContext;
import layr.api.Response;
import layr.commons.Listener;
import layr.exceptions.RoutingException;

public class RendererListener implements Listener<Response> {

    ApplicationContext configuration;
	RequestContext requestContext;
	
	public RendererListener(
			ApplicationContext configuration,
			RequestContext requestContext) {
    	this.configuration = configuration;
    	this.requestContext = requestContext;
	}

	@Override
	public void listen(Response response){
		BusinessRoutingRenderer renderer = new BusinessRoutingRenderer( configuration, requestContext );
		try {
			renderer.render(response);
		} catch (RoutingException e) {
			e.printStackTrace();
		}
	}
	
}
