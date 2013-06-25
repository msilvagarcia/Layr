package layr.jee;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import layr.exceptions.NotFoundException;

@WebFilter(filterName="layr.jee.Application", asyncSupported=true)
public class Application implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			runLifeCycle( request, response );
		} catch ( NotFoundException e ) {
			request.getServletContext().log("Not found");
			chain.doFilter( request, response );
		} catch (Exception e) {
			throw new IOException( e );
		}
	}

	public void runLifeCycle(ServletRequest request, ServletResponse response) throws Exception {
		JEELifeCycle lifeCycle = new JEELifeCycle( request, response );
		lifeCycle.run();
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		FilterRegistration filterRegistration = filterConfig.getServletContext().getFilterRegistration(filterConfig.getFilterName());
		for ( String urlPatternMapping : filterRegistration.getUrlPatternMappings() )
			filterConfig.getServletContext().log("Found Layr filter configured to pattern: " + urlPatternMapping );
	}

	@Override
	public void destroy() {}

}
