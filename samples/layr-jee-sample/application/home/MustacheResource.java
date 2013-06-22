package home;

import static me.msilvagarcia.layr.mustache.ResponseBuilder.*;

import java.util.ArrayList;
import java.util.List;

import layr.api.*;

@WebResource("mustache")
public class MustacheResource {

	@GET public Response render(){
		return template("template.mustache")
				.parameterObject(new Example("Miau", "Puppy", "Chiwaua"));
	}

	public class Example {

		String name;
		List<String> children;
		
		public Example( String name, String...children ) {
			this.name = name;
			this.children = new ArrayList<String>();
			for ( String child : children )
				this.children.add(child);
		}

	}
	
}
