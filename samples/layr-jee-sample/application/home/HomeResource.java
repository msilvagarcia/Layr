package home;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import layr.routing.annotations.Route;
import layr.routing.api.GET;
import layr.routing.api.Response;
import layr.routing.api.ResponseBuilder;
import layr.routing.api.TemplateParameter;
import layr.routing.api.WebResource;

@WebResource("home")
@Stateless
public class HomeResource {

	@EJB ProfileMeasurer profileMeasurer;
	@TemplateParameter String userName;

	@GET @Route
	public Response chooseARandomHomeScreen(){
		String templateName = ( profileMeasurer.measure() == 0 )
							? "home/default.xhtml"
							: "home/alternative.xhtml";

		if ( userName == null )
			userName = "Guest";

		return ResponseBuilder.renderTemplate( templateName );
	}
}
