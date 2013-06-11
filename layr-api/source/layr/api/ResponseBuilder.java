package layr.api;

import layr.api.TemplateResponse.TemplateOptionsResponse;

final public class ResponseBuilder {

	public static RedirectResponse redirectTo( String url ){
		return new ResponseImpl().redirectTo( url );
	}
	
	public static TemplateOptionsResponse template( String template ){
		return new ResponseImpl().template( template );
	}

	public static HeaderResponse header( String name, String value ){
		return new ResponseImpl().header( name, value );
	}
	
	public static StatusCodeResponse statusCode( int statusCode ) {
		return new ResponseImpl().statusCode(statusCode);
	}
	
	public static JSONResponse json(Object object) {
		return new ResponseImpl().json(object);
	}

}
