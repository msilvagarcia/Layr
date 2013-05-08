package layr.routing.jee;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import layr.routing.BusinessRoutingLifeCycle;
import layr.routing.ContainerRequestData;
import layr.routing.LifeCycle;
import layr.routing.NaturalRoutingLifeCycle;
import layr.routing.exceptions.NotFoundException;

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
		try {
			NaturalRoutingLifeCycle naturalRoutingLifeCycle = new NaturalRoutingLifeCycle( configuration );
			runLifeCycle(naturalRoutingLifeCycle, containerRequestData);
		} catch ( NotFoundException e ) {
			BusinessRoutingLifeCycle businessRoutingLifeCycle = new BusinessRoutingLifeCycle( configuration );
			runLifeCycle(businessRoutingLifeCycle, containerRequestData);
		}
	}

	public JEEConfiguration retrieveConfiguration(HttpServletRequest request) {
		return (JEEConfiguration) request.getServletContext().getAttribute(
				JEEConfiguration.class.getCanonicalName() );
	}

	public void runLifeCycle(LifeCycle lifeCycle, ContainerRequestData<?,?> containerRequestData) throws Exception {
		lifeCycle.createContext( containerRequestData );
		lifeCycle.run();
	}

}
