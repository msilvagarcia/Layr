package home;

import javax.ejb.EJB;

import layr.routing.annotations.Route;
import layr.routing.annotations.WebResource;


@WebResource("/home/")
public class HomeResource {

	@EJB ProfileMeasurer profileMeasurer;
	String templateName;
	String userName;

	@Route(
		pattern="/",
		template="#{templateName}")
	public void chooseARandomHomeScreen(){
		templateName = ( profileMeasurer.measure() == 0 )
							? "home/default.xhtml"
							: "home/alternative.xhtml";

		if ( userName == null )
			userName = "Guest";
	}

}
