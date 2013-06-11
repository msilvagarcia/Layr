package layr.api;

public interface TemplateResponse extends Response {
	TemplateOptionsResponse template(String template);

	public interface TemplateOptionsResponse extends HeaderResponse, StatusCodeResponse {
	
		TemplateOptionsResponse set(String name, Object value);
	
		TemplateOptionsResponse parameters(Object parameters);
	
		TemplateOptionsResponse encoding(String encoding);
	}

}

