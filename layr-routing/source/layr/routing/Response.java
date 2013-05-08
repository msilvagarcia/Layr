package layr.routing;

import java.util.HashMap;
import java.util.Map;

public class Response {

	String template;
	String redirectTo;
	Integer statusCode;
	String encoding;
	Map<String, String> headers;
	
	public Response() {
		headers = new HashMap<String, String>();
	}

	public Response renderTemplate( String template ) {
		this.template = template;
		return this;
	}

	public Response redirectTo( String url ) {
		this.redirectTo = url;
		return this;
	}

	public Response header( String name, String value ) {
		this.headers.put( name, value );
		return this;
	}

	public Response encoding( String encoding ) {
		this.encoding = encoding;
		return this;
	}
}
