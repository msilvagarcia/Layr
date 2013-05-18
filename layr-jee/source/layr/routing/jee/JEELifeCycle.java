package layr.routing.jee;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import layr.engine.RequestContext;
import layr.routing.exceptions.NotFoundException;
import layr.routing.lifecycle.BusinessRoutingLifeCycle;
import layr.routing.lifecycle.NaturalRoutingLifeCycle;

class JEELifeCycle {

	HttpServletRequest request;
	HttpServletResponse response;

	public JEELifeCycle(ServletRequest request, ServletResponse response) {
		this.request = (HttpServletRequest) request;
		this.response = (HttpServletResponse) response;
	}

	public void run() throws Exception {
		JEEContainerRequestData containerRequestData = new JEEContainerRequestData( request, response );
		JEEConfiguration configuration = retrieveConfiguration( request );
		RequestContext requestContext = configuration.createContext( containerRequestData );
		try {
			NaturalRoutingLifeCycle naturalRoutingLifeCycle = new NaturalRoutingLifeCycle( configuration, requestContext );
			naturalRoutingLifeCycle.run();
		} catch ( NotFoundException e ) {
			BusinessRoutingLifeCycle businessRoutingLifeCycle = new BusinessRoutingLifeCycle( configuration, requestContext );
			businessRoutingLifeCycle.run();
		}
	}

	public JEEConfiguration retrieveConfiguration(HttpServletRequest request) {
		return (JEEConfiguration) request.getServletContext().getAttribute(
				JEEConfiguration.class.getCanonicalName() );
	}

}
