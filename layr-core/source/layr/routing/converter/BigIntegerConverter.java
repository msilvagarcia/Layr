package layr.routing.converter;

import java.math.BigInteger;

import layr.exceptions.ConversionException;

public class BigIntegerConverter extends Converter<BigInteger> {

	@Override
	public BigInteger convert(String value, Class<BigInteger> clazz) throws ConversionException {
		return new BigInteger( value );
	}

}
