package layr.routing.api;

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
	
	public String template(){
		return template;
	}

	public Response redirectTo( String url ) {
		this.redirectTo = url;
		return this;
	}
	
	public String redirectTo(){
		return redirectTo;
	}

	public Response header( String name, String value ) {
		this.headers.put( name, value );
		return this;
	}
	
	public Map<String, String> headers(){
		return headers;
	}

	public Response encoding( String encoding ) {
		this.encoding = encoding;
		return this;
	}
	
	public String encoding(){
		return encoding;
	}
	
	public Response statusCode( int statusCode ) {
		this.statusCode = statusCode;
		return this;
	}
	
	public Integer statusCode(){
		return statusCode;
	}
}
