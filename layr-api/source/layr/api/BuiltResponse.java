package layr.api;

import java.util.Map;

public interface BuiltResponse {

	String redirectTo();

	Map<String, String> headers();

	String encoding();

	Integer statusCode();

	Map<String, Object> parameters();

	Object templateParameterObject();

	Object object();

	String contentType();
}
