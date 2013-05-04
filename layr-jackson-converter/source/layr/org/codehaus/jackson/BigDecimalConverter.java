package layr.org.codehaus.jackson;

import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimalConverter extends Converter<BigDecimal> {

	@Override
	public BigDecimal convert(String value, Class<BigDecimal> clazz) throws IOException {
		return new BigDecimal( value );
	}

}
