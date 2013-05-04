package layr.org.codehaus.jackson;

import java.io.IOException;
import java.math.BigInteger;

public class BigIntegerConverter extends Converter<BigInteger> {

	@Override
	public BigInteger convert(String value, Class<BigInteger> clazz) throws IOException {
		return new BigInteger( value );
	}

}
