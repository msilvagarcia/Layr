package layr.routing.sample;

import static layr.api.ResponseBuilder.*;
import layr.api.*;

@WebResource("home")
public class HomeResource {

	@GET
	public Response renderHome(){
		return template( "home.xhtml" )
				.set("user", new User( true ));
	}
}

class User {

	public Boolean isPremiumUser;

	public User( Boolean isPremiumUser ) {
		this.isPremiumUser = isPremiumUser;
	}
}