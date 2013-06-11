package home;

import static layr.api.ResponseBuilder.*;
import layr.api.*;
import javax.ejb.*;

@WebResource("home")
@Stateless
public class HomeResource {

	@EJB ProfileMeasurer profileMeasurer;

	@GET
	public Response chooseARandomHomeScreen(
			@QueryParameter("userName") String userName){
		String templateName = ( profileMeasurer.measure() == 0 )
							? "home/default.xhtml"
							: "home/alternative.xhtml";

		if ( userName == null )
			userName = "Guest";

		return template( templateName )
				.set("userName", userName);
	}
}
