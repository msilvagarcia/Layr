package layr.api;

public interface StatusCodeResponse extends HeaderResponse {
	StatusCodeResponse statusCode(int statusCode);
}

