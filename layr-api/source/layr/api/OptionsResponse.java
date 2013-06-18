package layr.api;

public interface OptionsResponse extends HeaderResponse, StatusCodeResponse {

	OptionsResponse set(String name, Object value);

	OptionsResponse parameters(Object parameters);

	OptionsResponse encoding(String encoding);
}

