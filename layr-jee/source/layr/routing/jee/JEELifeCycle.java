package layr.routing.jee;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		JEEConfiguration configuration = retrieveConfiguration( request );
		JEERequestContext requestContext = createContext( configuration );
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

	public JEERequestContext createContext(JEEConfiguration configuration){
		JEERequestContext requestContext = new JEERequestContext(request, response);
		requestContext.setRegisteredTagLibs(configuration.getRegisteredTagLibs());
		requestContext.setIsAsyncRequest(isAsyncRequest());
		requestContext.setDefaultResource(configuration.getDefaultResource());
		return requestContext;
	}

	public Boolean isAsyncRequest() {
		return request.isAsyncSupported();
	}
}
