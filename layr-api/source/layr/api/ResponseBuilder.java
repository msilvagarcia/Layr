package layr.api;

final public class ResponseBuilder {

	public static RedirectResponse redirectTo( String url ){
		return new ResponseImpl().redirectTo( url );
	}

	public static OptionsResponse template( String template ){
		return new ResponseImpl()
			.contentType("text/html")
			.object( template );
	}

	public static HeaderResponse header( String name, String value ){
		return new ResponseImpl().header( name, value );
	}

	public static StatusCodeResponse statusCode( int statusCode ) {
		return new ResponseImpl().statusCode(statusCode);
	}

	public static OptionsResponse json(Object object) {
		return new ResponseImpl()
				.contentType("application/json")
				.object( object );
	}
}
