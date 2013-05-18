package user;

import java.util.Date;

import layr.routing.annotations.Route;
import layr.routing.api.PathParameter;
import layr.routing.api.QueryParameter;
import layr.routing.api.Response;
import layr.routing.api.ResponseBuilder;
import layr.routing.api.TemplateParameter;
import layr.routing.api.WebResource;

@WebResource("user")
public class UserResource {

	@QueryParameter
	@TemplateParameter String userName;

	/**
	 * @param userId
	 */
	@Route( pattern="/{id}/edit",
			template="user/edit.xhtml" )
	public Response editUser( @PathParameter("id") Long userId )
	{
		if ( haveUserBillingPendencies() )
			return ResponseBuilder.redirectTo( "/user/warning/" );
		return null;
	}

	/**
	 * @return
	 */
	public boolean haveUserBillingPendencies(){
		return new Date().getTime() % 2 == 0;
	}

}
