package layr.routing.sample;

import static layr.api.ResponseBuilder.*;
import layr.api.*;
import layr.routing.impl.Blah;

@WebResource("home")
@Blah
public class HomeResource {
	
	String injected;

	@GET
	public Response renderHome(){
		return template( "home.xhtml" )
				.set("user", new User( true ))
				.set("injected", injected);
	}
}

class User {

	public Boolean isPremiumUser;

	public User( Boolean isPremiumUser ) {
		this.isPremiumUser = isPremiumUser;
	}
}