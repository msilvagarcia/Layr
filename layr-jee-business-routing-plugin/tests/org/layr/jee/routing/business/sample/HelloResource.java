package org.layr.jee.routing.business.sample;

import java.util.List;

import org.layr.jee.routing.business.Parameter;
import org.layr.jee.routing.business.Route;
import org.layr.jee.routing.business.WebResource;



@WebResource(rootURL="/hello/")
public class HelloResource{

	Hello hello;
	List<World> worlds;
	double sum;
	String output;

	@Route
	public String sayHello() {
		return "Hello World";
	}

	@Route(pattern="/say/#{id}/something/", template="/hello.xhtml")
	public void say(
				@Parameter("id") Integer id1,
				@Parameter("hello.realworld.id") Long id2)
	{
		output = "Id '"+id1+"' and '"+id2+"' arrived from request.";
	}
	
	@Route(pattern="/add", template="/hello.xhtml")
	public void create(
			@Parameter("name") String name) {
		output = name;
	}
	
	@Route(pattern="/addAsJSON")
	public Hello createAsJSON(
			@Parameter("name") String name) {
		Hello hello = new Hello();
		hello.setWorld(name);
		return hello;
	}

	public Hello getHello() {
		return hello;
	}

	public void setHello(Hello hello) {
		this.hello = hello;
	}

	public double getSum() {
		return sum;
	}

	public void setSum(Double sum) {
		this.sum = sum;
	}

	public List<World> getWorlds() {
		return worlds;
	}

	public void setWorlds(List<World> worlds) {
		this.worlds = worlds;
	}
}
