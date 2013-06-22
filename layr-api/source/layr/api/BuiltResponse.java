package layr.api;

import java.util.Map;

public interface BuiltResponse {

	String redirectTo();

	Map<String, String> headers();

	String encoding();

	Integer statusCode();

	Map<String, Object> parameters();

	Object parameterObject();

	/**
	 * @return a template name when needed by its respective {@link OutputRenderer}. It's
	 * optional.
	 */
	String templateName();

	/**
	 * @return the response Content-Type. It is used to define which {@link OutputRenderer}
	 * will be used to render the response.
	 */
	String contentType();
}
