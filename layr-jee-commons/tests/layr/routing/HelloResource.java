package layr.routing;

import layr.routing.annotations.*;

@WebResource("hello")
public class HelloResource{

	@PathParameter Long pathParamOnBody;
	@QueryParameter Long requestParamOnBody;

	@GET
	@Route("world/{pathParamOnBody}")
	public Response renderWithResponseBuilder(){
		return ResponseBuilder
				.renderTemplate( "hello.xhtml" );
	}

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
}
