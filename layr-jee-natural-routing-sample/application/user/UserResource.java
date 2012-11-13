package user;

import java.util.Date;

import org.layr.jee.routing.business.Parameter;
import org.layr.jee.routing.business.Route;
import org.layr.jee.routing.business.WebResource;

@WebResource("/user/")
public class UserResource {

	String redirectTo = "";
	
	@Route(
		pattern="/#{id}/edit",
		template="user/editForm.xhtml",
		redirectTo="#{redirectTo}" )
	public void editUser(
		@Parameter("id") Long userId ){
		if ( haveUserBillingPendencies() ){
			redirectTo = "/user/warning/";
			return;
		}
		
		// load user information with userId.
	}

	public boolean haveUserBillingPendencies(){
		return new Date().getTime() % 2 == 0;
	}

}
