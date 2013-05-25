package layr.routing.lifecycle;

import java.text.SimpleDateFormat;
import java.util.Date;

import layr.api.RequestContext;
import layr.api.Response;
import layr.commons.Listener;
import layr.exceptions.RoutingException;
import layr.routing.impl.StubRequestContext;

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
			SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss.SSSS");
			String date = dateFormat.format(new Date());
			System.out.println(date + " " + ((StubRequestContext)requestContext).getBufferedWroteContentToOutput());
		} catch (RoutingException e) {
			e.printStackTrace();
		}
	}
	
}
