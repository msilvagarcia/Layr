package layr.routing.api;

public interface HeaderResponse extends Response {
	HeaderResponse header(String name, String value);
}
