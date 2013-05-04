package layr.org.codehaus.jackson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.junit.Test;

public class ConversionTest {

	ConverterFactory factory;

	public ConversionTest() {
		factory = new ConverterFactory();
	}

	@Test
	public void grantThatConvertLong() throws ConversionException{
		Long converted = factory.convert( "123", Long.class );
		assertEquals( 123L, converted.longValue() );
	}

	@Test
	public void grantThatConvertInteger() throws ConversionException{
		Integer converted = factory.convert( "123", Integer.class );
		assertEquals( 123L, converted.intValue() );
	}

	@Test
	public void grantThatConvertByte() throws ConversionException{
		Byte converted = factory.convert( "7", Byte.class );
		assertEquals( (byte)7, converted.byteValue() );
	}

	@Test
	public void grantThatConvertFloat() throws ConversionException{
		Float converted = factory.convert( "12.3", Float.class );
		assertEquals( (float)12.3, converted.floatValue(), 0 );
	}

	@Test
	public void grantThatConvertDouble() throws ConversionException{
		Double converted = factory.convert( "12.3", Double.class );
		assertEquals( 12.3D, converted.doubleValue(), 0 );
	}

	@Test
	public void grantThatConvertBigDecimal() throws ConversionException{
		BigDecimal converted = factory.convert( "123", BigDecimal.class );
		assertEquals( new BigDecimal(123), converted );
	}

	@Test
	public void grantThatConvertBigInteger() throws ConversionException{
		BigInteger converted = factory.convert( "123", BigInteger.class );
		assertEquals( new BigInteger("123"), converted );
	}

	@Test
	public void grantThatConvertShort() throws ConversionException{
		Short converted = factory.convert( "123", Short.class );
		assertEquals( (short)123, converted.intValue() );
	}

	@Test
	public void grantThatConvertBoolean() throws ConversionException{
		Boolean converted = factory.convert( "true", Boolean.class );
		assertEquals( true, converted );
	}

	@Test
	public void grantThatConvertString() throws ConversionException{
		String converted = factory.convert( "true", String.class );
		assertEquals( "true", converted );
	}

	@Test
	public void grantThatConvertNull() throws ConversionException{
		Integer converted = factory.convert( null, Integer.class );
		assertNull( converted );
	}

	@Test
	public void grantThatConvertObject() throws ConversionException{
		String json = "{ \"name\":\"Helden\", \"addresses\":[ { \"fullAddress\":\"R. Getulio Vargas, 0 - Joinville\" } ] }";
		PeopleObject converted = factory.convert( json, PeopleObject.class );
		assertEquals( "Helden", converted.getName() );

		List<Address> addresses = converted.getAddresses();
		assertEquals( 1, addresses.size() );
		assertEquals( "R. Getulio Vargas, 0 - Joinville", addresses.get(0).getFullAddress() );
	}
}
