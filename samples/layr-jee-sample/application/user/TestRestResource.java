package user;

import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("blah")
@Stateless
public class TestRestResource {

	@GET public String doHelloWorld() throws NamingException{
		InitialContext initialContext = new InitialContext();
		System.out.println(initialContext.lookup("java:module/HomeResource"));
		return "Hello World";
	}
}
