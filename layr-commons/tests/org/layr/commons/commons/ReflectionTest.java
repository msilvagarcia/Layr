package org.layr.commons.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.layr.commons.Reflection;


public class ReflectionTest {

	@Test
	public void grantThatRetrieveAttributesFromUser() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		String userName = "Helden";

		User user = new User();
		user.setName(userName);

		assertEquals( userName, Reflection.getAttribute(user, "name") );
		assertEquals( "helden@home", Reflection.getAttribute(user, "email") );
		assertNull( Reflection.getAttribute(user, "username") );
	}
	
	public class User {

		private String name;

		public void setName(String name) {
			this.name = name;
		}
		
		public String getEmail(){
			return name.toLowerCase() + "@home";
		}
	}

}
