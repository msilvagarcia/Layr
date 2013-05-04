package layr.org.codehaus.jackson;

import java.util.HashMap;
import java.util.Map;

public class ConverterFactory {
	
	Map<String, Class<? extends Converter<?>>> converters;

	public ConverterFactory() {
		converters = new HashMap<String, Class<? extends Converter<?>>>();
		registerKnownConverters();
	}
	
	@SuppressWarnings("unchecked")
	public void registerKnownConverters(){
		register(
			ByteConverter.class, ShortConverter.class, IntegerConverter.class,
			FloatConverter.class, DoubleConverter.class, BooleanConverter.class,
			LongConverter.class, BigDecimalConverter.class, BigIntegerConverter.class );
	}

	public void register( Class<? extends Converter<?>>...converters ) {
		for ( Class<? extends Converter<?>> converter : converters )
			try {
				register( converter.newInstance() );
			} catch (Throwable e) {
				continue;
			}
	}

	@SuppressWarnings("unchecked")
	public void register(Converter<?> newInstance) {
		register( newInstance.getGenericClass(), 
			(Class<? extends Converter<?>>)newInstance.getClass() );
	}

	public void register(Class<?> targetClass, Class<? extends Converter<?>> converter){
		this.converters.put( targetClass.getCanonicalName(), converter );
	}

	@SuppressWarnings("unchecked")
	public <T> T convert( String value, Class<T> clazz ) throws ConversionException {
		try {
			if ( value == null
			||   String.class.equals( clazz ) )
				return (T) value;
			Converter<T> converter = getConverterFor( clazz );
			return converter.convert( value, clazz );
		} catch (Exception e) {
			throw new ConversionException( String.format(
					"Can't convert '%s' to '%s'", value, clazz.getCanonicalName()), e );
		}
	}

	@SuppressWarnings("unchecked")
	public <T> Converter<T> getConverterFor( Class<T> clazz )
			throws InstantiationException, IllegalAccessException {
		Class<? extends Converter<T>> converterClass = 
			(Class<? extends Converter<T>>) converters.get( clazz.getCanonicalName() );
		if ( converterClass == null )
			return (Converter<T>) new JacksonConverter();
		return (Converter<T>)converterClass.newInstance();
	}
}
