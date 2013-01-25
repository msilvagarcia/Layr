package org.layr.engine.expressions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;
import org.layr.engine.IRequestContext;
import org.layr.engine.expressions.ComplexExpressionEvaluator;
import org.layr.engine.expressions.ExpressionEvaluator;
import org.layr.engine.sample.StubRequestContext;

public class ExpressionTest {

	private static final String COMPONENT_PATTERN_NAME = "#{Component:usuario}blah";
	private static final String COMPONENT_COMPOSED_PATTERN_NAME = "#{Component:usuario.nome}blah";
	private static final String CONTEXT = "#{usuario.context}";
	private static final String HELLO = "Olá #{usuario.nome}!";
	private static final String NAME = "#{nome}";
	private static final String TWO_EXPRESSIONS = "#{usuario.nome} will #{usuario.action} till tomorrow.";

	private IRequestContext layrContext;

	@Before
	public void setup() throws IOException, ClassNotFoundException, ServletException{
		layrContext = new StubRequestContext();
	}

	@Test
	public void evalTest() {
		Usuario usuario = new Usuario("Miere", layrContext);
		layrContext.put("usuario", usuario);

		String returnString = ComplexExpressionEvaluator.getValue(HELLO, layrContext ).toString();
		assertEquals("Olá Miere!", returnString);

		Object evaluatedObject = ComplexExpressionEvaluator.getValue(CONTEXT, layrContext);
		assertSame(layrContext, evaluatedObject);

		String newName = "Joseh";
		//LayrBinder.setValue(NAME, context, newName);
		ExpressionEvaluator.eval(usuario, NAME).setValue(newName);
		assertEquals(newName, usuario.getNome());
	}
	
	@Test
	public void evalTwoExpressions() {
		Usuario usuario = new Usuario("Miere", layrContext);
		usuario.setAction("run");
		layrContext.put("usuario", usuario);

		String returnString = ComplexExpressionEvaluator.getValue(TWO_EXPRESSIONS, layrContext ).toString();
		assertEquals("Miere will run till tomorrow.", returnString);
	}
	
	@Test
	public void evalComponentPattern() {
		layrContext.put("Component:usuario", "blah");

		Object value = ComplexExpressionEvaluator.getValue(COMPONENT_PATTERN_NAME, layrContext );
		assertNotNull(value);
		String returnString = value.toString();
		assertEquals("blahblah", returnString);
	}
	
	@Test
	public void evalAnotherComponentPattern() {
		layrContext.put("Component:usuario", new Usuario("Miere",null));

		Object value = ComplexExpressionEvaluator.getValue(COMPONENT_COMPOSED_PATTERN_NAME, layrContext );
		assertNotNull(value);
		String returnString = value.toString();
		assertEquals("Miereblah", returnString);
	}
	
	@Test
	public void evalExpressionWhenTheresNoPlaceholderButThisIsSetAtContext(){
		String expected = "Miere";
		layrContext.put("nome", expected);
		Object value = ComplexExpressionEvaluator.getValue(NAME, layrContext );
		assertNotNull(value);
		String returnString = value.toString();
		assertEquals(expected, returnString);
	}

	public class Usuario {
		private String nome;
		private String action;
		private IRequestContext context;
		
		public Usuario(String nome, IRequestContext context) {
			setContext(context);
			setNome(nome);
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public String getNome() {
			return nome;
		}

		public void setContext(IRequestContext context) {
			this.context = context;
		}

		public IRequestContext getContext() {
			return context;
		}

		public void setAction(String action) {
			this.action = action;
		}

		public String getAction() {
			return action;
		}
	}
}
