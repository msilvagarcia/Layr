package user;

import java.util.Date;

import layr.routing.annotations.PathParameter;
import layr.routing.annotations.QueryParameter;
import layr.routing.annotations.Route;
import layr.routing.annotations.TemplateParameter;
import layr.routing.annotations.WebResource;
import layr.routing.api.Response;
import layr.routing.service.ResponseBuilder;

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
