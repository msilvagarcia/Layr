package org.layr.commons.commons;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;


import org.junit.Test;
import org.layr.commons.Converter;
import org.layr.commons.Reflection;
import org.layr.commons.sample.StringConverter;

import static org.junit.Assert.*;


public class ReflectionTest {

	@Test
	public void retrieveAnnotatedFields() {
		User user = new AnotherUser();
		List<Field> fields = Reflection.extractAnnotatedFieldsFor(Deprecated.class, user);
		assertEquals(2, fields.size());
		assertEquals("active", fields.get(0).getName());
		assertEquals("name", fields.get(1).getName());
	}

	@Test
	public void grantThatRetrieveAttributesFromUser() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		String userName = "Helden";

		User user = new User();
		user.setName(userName);

		assertEquals( userName, Reflection.getAttribute(user, "name") );
		assertEquals( "helden@home", Reflection.getAttribute(user, "email") );
		assertNull( Reflection.getAttribute(user, "username") );
	}

	@Test
	public void grantThatRetrieveEncodedAttributesFromUser() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException{
		User user = new User();
		user.setName("Helden");

		assertEquals( "<Helden>", Reflection.getAttributeAndEncodeReturnedValue(user, "name") );
		assertEquals( "helden@home", Reflection.getAttributeAndEncodeReturnedValue(user, "email") );
		assertNull( Reflection.getAttributeAndEncodeReturnedValue(user, "username") );
	}

	@Test
	public void grantThatSetAttributeIntoUser() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		String userName = "Helden";
		User user = new User();
		Reflection.setAttribute(user, "name", userName);
		assertEquals( userName, user.name );
	}
	
	public class User {
		@Deprecated
		@Converter(StringConverter.class)
		private String name;

		public void setName(String name) {
			this.name = name;
		}
		
		public String getEmail(){
			return name.toLowerCase() + "@home";
		}
	}
	
	public class AnotherUser extends User {
		@Deprecated
		private Boolean active;

		public void setActive(Boolean active) {
			this.active = active;
		}

		public Boolean getActive() {
			return active;
		}
	}
}
