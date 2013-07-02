package layr.routing.lifecycle;

import static layr.api.ResponseBuilder.template;

import java.util.ArrayList;
import java.util.List;

import layr.api.ApplicationContext;
import layr.api.ExceptionHandler;
import layr.api.RequestContext;
import layr.api.Response;

public class DefaultUnhandledExceptionHandler implements ExceptionHandler<Throwable> {

	@Override
	public Response render(
			ApplicationContext applicationContext, RequestContext requestContext,
				Throwable exception) {
		exception.printStackTrace();
		List<StackTraceElement> list = extractStackTraceAsList(exception);
		return template("layr/routing/lifecycle/DefaultUnhandledExceptionTemplate.xhtml")
				.set("stack", list)
				.set("errorMessage", exception.getMessage());
	}

	private List<StackTraceElement> extractStackTraceAsList(Throwable exception) {
		ArrayList<StackTraceElement> list = new ArrayList<StackTraceElement>();
		
		for ( StackTraceElement stackTraceElement : exception.getStackTrace() )
			list.add(stackTraceElement);
		return list;
	}
}
