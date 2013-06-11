package layr.routing.lifecycle;

import layr.api.RequestContext;
import layr.commons.Listener;
import layr.exceptions.RoutingException;

public class RendererListener implements Listener<Object> {

    ApplicationContext configuration;
	RequestContext requestContext;
	
	public RendererListener(
			ApplicationContext configuration,
			RequestContext requestContext) {
    	this.configuration = configuration;
    	this.requestContext = requestContext;
	}

	@Override
	public void listen(Object response){
		BusinessRoutingRenderer renderer = new BusinessRoutingRenderer( configuration, requestContext );
		try {
			renderer.render(response);
		} catch (RoutingException e) {
			e.printStackTrace();
		}
	}
	
}
