package layr.routing.async;

import static layr.api.ResponseBuilder.*;
import layr.api.GET;
import layr.api.Response;
import layr.api.WebResource;

@WebResource("stress")
public class StressTemplateResource {

	@GET public Response render(){
		return template("stress-template.xhtml");
	}

}
