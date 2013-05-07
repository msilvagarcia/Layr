package layr.routing.jee;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;

import layr.routing.exceptions.NotFoundException;

@WebServlet
public class Application implements Servlet {

	@Override
	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		try {
			runLifeCycle( request, response );
		} catch ( NotFoundException e ) {
			throw new ServletException( "No found", e );
		} catch (Exception e) {
			throw new IOException( e );
		}
	}

	public void runLifeCycle(ServletRequest request, ServletResponse response) throws Exception {
		JEELifeCycle lifeCycle = new JEELifeCycle( request, response );
		lifeCycle.run();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {}

	@Override
	public void destroy() {}

	@Override
	public ServletConfig getServletConfig() {
		return null;
	}

	@Override
	public String getServletInfo() {
		return null;
	}

}
