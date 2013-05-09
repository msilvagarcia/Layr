package layr.routing;

import layr.routing.annotations.Route;
import layr.routing.annotations.TemplateParameter;
import layr.routing.annotations.WebResource;

@WebResource("home")
public class HomeResource {
	
	@TemplateParameter User user; 

	@Route( template="home.xhtml" )
	public void renderHome(){
		user = new User( true );
	}
}

class User {

	Boolean isPremiumUser;

	public User( Boolean isPremiumUser ) {
		this.isPremiumUser = isPremiumUser;
	}
}