package layr.routing.jee;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import layr.exceptions.NotFoundException;

@WebFilter(filterName="layr.routing.jee.Application")
public class Application implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			runLifeCycle( request, response );
		} catch ( NotFoundException e ) {
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
	public void init(FilterConfig filterConfig) throws ServletException {}

	@Override
	public void destroy() {}

}
