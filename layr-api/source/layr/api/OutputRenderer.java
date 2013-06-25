package layr.api;

import java.io.IOException;

public interface OutputRenderer {

	void render( RequestContext requestContext, BuiltResponse response ) throws IOException;

}
