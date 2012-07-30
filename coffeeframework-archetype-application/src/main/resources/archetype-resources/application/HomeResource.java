#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import layr.annotation.Action;
import layr.annotation.WebResource;

@WebResource(
	rootURL = "/home/",
	template="${package}/home.xhtml")

public class HomeResource {

	@Action( pattern = "/")
	public void home() {}

}
