package user;

import java.util.Date;

import layr.routing.annotations.PathParameter;
import layr.routing.annotations.Route;
import layr.routing.annotations.WebResource;


@WebResource("/user/")
public class UserResource {

	String redirectTo;
	String userName;

	/**
	 * @param userId
	 */
	@Route( pattern="/#{id}/edit",
			template="user/edit.xhtml",
			redirectTo="#{redirectTo}" )
	public void editUser(
		@PathParameter("id") Long userId ){
		if ( haveUserBillingPendencies() ){
			redirectTo = "/user/warning/";
			return;
		}

		//TODO: load user information with userId.
	}

	/**
	 * @return
	 */
	public boolean haveUserBillingPendencies(){
		return new Date().getTime() % 2 == 0;
	}

}
