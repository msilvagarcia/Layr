package layr.api;

import layr.api.TemplateResponse.TemplateOptionsResponse;

final public class ResponseBuilder {

	public static RedirectResponse redirectTo( String url ){
		return new ResponseImpl().redirectTo( url );
	}
	
	public static TemplateOptionsResponse renderTemplate( String template ){
		return new ResponseImpl().renderTemplate( template );
	}

	public static HeaderResponse header( String name, String value ){
		return new ResponseImpl().header( name, value );
	}
	
	public static StatusCodeResponse statusCode( int statusCode ) {
		return new ResponseImpl().statusCode(statusCode);
	}

}
