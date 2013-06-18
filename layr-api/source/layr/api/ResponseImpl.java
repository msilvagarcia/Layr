package layr.api;

import java.util.HashMap;
import java.util.Map;

public class ResponseImpl
implements 
	OptionsResponse, RedirectResponse,
	HeaderResponse, StatusCodeResponse, BuiltResponse {

	String contentType;
	String redirectTo;
	Integer statusCode;
	String encoding;
	Map<String, String> headers;
	Map<String, Object> parameters;
	Object parameterObject;
	Object object;

	public ResponseImpl() {
		headers = new HashMap<String, String>();
		parameters = new HashMap<String, Object>();
	}

	public OptionsResponse object(Object object) {
		this.object = object;
		return this;
	}

	public Object object(){
		return object;
	}

	public ResponseImpl redirectTo(String url) {
		this.redirectTo = url;
		return this;
	}

	public String redirectTo() {
		return redirectTo;
	}

	public ResponseImpl header(String name, String value) {
		this.headers.put(name, value);
		return this;
	}

	public Map<String, String> headers() {
		return headers;
	}

	public ResponseImpl encoding(String encoding) {
		this.encoding = encoding;
		return this;
	}

	public String encoding() {
		return encoding;
	}

	public ResponseImpl statusCode(int statusCode) {
		this.statusCode = statusCode;
		return this;
	}

	public Integer statusCode() {
		return statusCode;
	}

	public ResponseImpl parameters(Object parameters) {
		this.parameterObject = parameters;
		return this;
	}

	public ResponseImpl set(String name, Object value) {
		this.parameters.put(name, value);
		return this;
	}

	public Map<String, Object> parameters() {
		return parameters;
	}

	public Object templateParameterObject() {
		return parameterObject;
	}
	
	public ResponseImpl contentType(String contentType) {
		this.contentType = contentType;
		return this;
	}
	
	@Override
	public String contentType() {
		return contentType;
	}

	@Override
	public ResponseImpl build() {
		return this;
	}
}
