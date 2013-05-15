package layr.routing.service;

import layr.routing.api.Response;

final public class ResponseBuilder {

	public static Response redirectTo( String url ){
		return new Response().redirectTo( url );
	}
	
	public static Response renderTemplate( String template ){
		return new Response().renderTemplate( template );
	}

	public static Response header( String name, String value ){
		return new Response().header( name, value );
	}

}
