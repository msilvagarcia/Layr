package layr.routing;

import layr.routing.api.*;

@WebResource("home")
public class HomeResource {
	
	@TemplateParameter User user; 

	@GET
	public Response renderHome(){
		user = new User( true );
		return ResponseBuilder.renderTemplate( "home.xhtml" );
	}
}

class User {

	Boolean isPremiumUser;

	public User( Boolean isPremiumUser ) {
		this.isPremiumUser = isPremiumUser;
	}
}