package layr.routing.api;

import java.util.Map;

public interface BuiltResponse {

	String template();

	String redirectTo();

	Map<String, String> headers();

	String encoding();

	Integer statusCode();

	Map<String, Object> parameters();

	Object templateParameterObject();
}
