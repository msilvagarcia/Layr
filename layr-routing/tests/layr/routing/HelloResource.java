package layr.routing;

import java.io.IOException;

import layr.routing.api.DELETE;
import layr.routing.api.GET;
import layr.routing.api.POST;
import layr.routing.api.PUT;
import layr.routing.api.PathParameter;
import layr.routing.api.QueryParameter;
import layr.routing.api.Response;
import layr.routing.api.ResponseBuilder;
import layr.routing.api.TemplateParameter;
import layr.routing.api.WebResource;

@WebResource("hello")
public class HelloResource{

	@TemplateParameter
	@PathParameter
	Long pathParamOnBody;

	@TemplateParameter
	@QueryParameter
	Double requestParamOnBody;

	@GET("world/{pathParamOnBody}")
	public Response renderThroughResponseBuilder(){
		return ResponseBuilder
				.renderTemplate( "hello.xhtml" );
	}

	@PUT("world/{pathParamOnBody}")
	public Response renderThroughAnnotation(){
		return ResponseBuilder.renderTemplate("hello.xhtml");
	}

	@POST("world/{param1}/{param2}")
	public Response sendRedirectionWithResponseBuilder(
			@PathParameter("param1") String param1,
			@PathParameter("param2") Double param2,
			@QueryParameter("isSomething") Boolean isSomething){
		return ResponseBuilder
				.redirectTo( String.format(
					"/response/%s/%s/%s/", param1, param2, isSomething) );
	}
	
	@DELETE("world")
	public void doSomethingButDoNotRenderTemplate(){}
	
	@GET("handled/error")
	public void handledError(){
		throw new NullPointerException();
	}

	@GET("unhandled/error")
	public void unhandledError() throws IOException{
		throw new IOException();
	}
}
