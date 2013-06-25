package user;

import static layr.api.ResponseBuilder.*;
import layr.api.*;
import java.util.Date;

@WebResource("user")
public class UserResource {

	@GET("/{id}/edit")
	public Response editUser( @PathParameter("id") Long userId ) {
		if ( haveUserBillingPendencies() )
			return redirectTo( "/user/warning/" );
		return template("user/edit.xhtml");
	}

	public boolean haveUserBillingPendencies(){
		return new Date().getTime() % 2 == 0;
	}

}
