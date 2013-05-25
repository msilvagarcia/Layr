package layr.routing.async;

import static layr.routing.api.ResponseBuilder.*;
import layr.routing.api.GET;
import layr.routing.api.Response;
import layr.routing.api.WebResource;

@WebResource("stress")
public class StressTemplateResource {

	@GET public Response render(){
		return renderTemplate("stress-template.xhtml");
	}

}
