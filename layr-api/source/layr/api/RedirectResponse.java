package layr.api;

public interface RedirectResponse extends Response {
	RedirectResponse redirectTo(String url);
}
