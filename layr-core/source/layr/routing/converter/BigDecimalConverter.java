package layr.routing.converter;

import java.math.BigDecimal;

import layr.exceptions.ConversionException;

public class BigDecimalConverter extends Converter<BigDecimal> {

	@Override
	public BigDecimal convert(String value, Class<BigDecimal> clazz) throws ConversionException {
		return new BigDecimal( value );
	}

}
