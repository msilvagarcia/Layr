package layr.routing.sample;

import static layr.routing.api.ResponseBuilder.*;
import layr.routing.api.*;

@WebResource("home")
public class HomeResource {

	@GET
	public Response renderHome(){
		return renderTemplate( "home.xhtml" )
				.set("user", new User( true ));
	}
}

class User {

	public Boolean isPremiumUser;

	public User( Boolean isPremiumUser ) {
		this.isPremiumUser = isPremiumUser;
	}
}