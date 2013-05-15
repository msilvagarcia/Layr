package layr.routing;

import java.io.IOException;

import layr.routing.annotations.*;
import layr.routing.api.Response;
import layr.routing.service.ResponseBuilder;

@WebResource("hello")
public class HelloResource{

	@TemplateParameter
	@PathParameter
	Long pathParamOnBody;

	@TemplateParameter
	@QueryParameter
	Double requestParamOnBody;

	@GET
	@Route("world/{pathParamOnBody}")
	public Response renderThroughResponseBuilder(){
		return ResponseBuilder
				.renderTemplate( "hello.xhtml" );
	}

	@PUT
	@Route(pattern="world/{pathParamOnBody}", template="hello.xhtml")
	public void renderThroughAnnotation(){}

	@POST
	@Route("world/{param1}/{param2}")
	public Response sendRedirectionWithResponseBuilder(
			@PathParameter("param1") String param1,
			@PathParameter("param2") Double param2,
			@QueryParameter("isSomething") Boolean isSomething){
		return ResponseBuilder
				.redirectTo( String.format(
					"/response/%s/%s/%s/", param1, param2, isSomething) );
	}
	
	@DELETE
	@Route("world")
	public void doSomethingButDoNotRenderTemplate(){}
	
	@GET
	@Route("handled/error")
	public void handledError(){
		throw new NullPointerException();
	}

	@GET
	@Route("unhandled/error")
	public void unhandledError() throws IOException{
		throw new IOException();
	}
}
