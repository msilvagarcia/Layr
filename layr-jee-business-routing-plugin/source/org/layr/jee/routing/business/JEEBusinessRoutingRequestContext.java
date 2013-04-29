package org.layr.jee.routing.business;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.layr.engine.expressions.URLPattern;
import org.layr.jee.commons.JEEConfiguration;
import org.layr.jee.commons.JEERequestContext;

public class JEEBusinessRoutingRequestContext extends JEERequestContext {

	public JEEBusinessRoutingRequestContext(
			HttpServletRequest request, HttpServletResponse response,
			JEEConfiguration configuration ) {
		super( request, response, configuration );
	}

	private String requestRoute;
	private String requestRoutePattern;

	public String getRequestedRoute() {
		return requestRoute;
	}

	public void setRequestRoute(String requestRoute) {
		this.requestRoute = requestRoute;
	}

	public String getRequestedRoutePattern() {
		return requestRoutePattern;
	}

	public void setRequestRoutePattern(String requestRoutePattern) {
		this.requestRoutePattern = requestRoutePattern;
	}

	/**
	 * Extract the parameter sent to the routing method through the
	 * route's URL pattern. For example: it returns a Map with
	 * the 'id' key set to 9834 when the routing method's pattern is
	 * /user/#{id}/edit and the request URL is /user/9834/edit.
	 * 
	 * @return
	 */
	public Map<String, String> getRequestParamsFromRoutePattern(){
		if ( getRequestedRoutePattern() == null 
		||   getRequestedRoute() == null )
			return null;

		return new URLPattern()
					.extractMethodPlaceHoldersValueFromURL(
						getRequestedRoutePattern(),
						getRequestedRoute());
	}

	/**
	 * @param request
	 * @param response
	 * @param context
	 * @return
	 */
	public static JEEBusinessRoutingRequestContext createRequestContext(ServletRequest request,
			ServletResponse response, ServletContext context) {
		JEEBusinessRoutingConfiguration configuration = (JEEBusinessRoutingConfiguration)context.getAttribute( JEEBusinessRoutingConfiguration.class.getCanonicalName() );
		JEEBusinessRoutingRequestContext requestContext = new JEEBusinessRoutingRequestContext(
				(HttpServletRequest)request, (HttpServletResponse)response, configuration);
		configureRequestContext( requestContext );
		return requestContext;
	}
}
