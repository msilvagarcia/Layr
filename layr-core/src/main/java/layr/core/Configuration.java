package layr.core;

public class Configuration {
	
	private boolean isCacheEnabled;
	private boolean isEquationsDisabled;
	private String defaultResource;

	public Configuration() {
		setEquationsDisabled( readSystemProperty("equationDisabled", "false") );
		setCacheEnabled( readSystemProperty("cacheEnabled", "false") );
		setDefaultResource( "/theme/" );
	}

	public String readSystemProperty( String propertyName, String defaultValue ){
		String propertyValue = System.getProperty("layr.config." + propertyName);
		if ( propertyValue == null || propertyValue.isEmpty() )
			propertyValue = defaultValue;
		return propertyValue;
	}

	public boolean isCacheEnabled() {
		return isCacheEnabled;
	}

	public void setCacheEnabled(String isCacheEnabled){
		setCacheEnabled( Boolean.valueOf(isCacheEnabled) );
	}

	public void setCacheEnabled(boolean isCacheEnabled) {
		this.isCacheEnabled = isCacheEnabled;
	}

	public boolean isEquationsDisabled() {
		return isEquationsDisabled;
	}

	public void setEquationsDisabled(String isEquationsDisabled) {
		setEquationsDisabled( Boolean.valueOf(isEquationsDisabled) );
	}
	
	public void setEquationsDisabled(boolean isEquationsDisabled) {
		this.isEquationsDisabled = isEquationsDisabled;
	}

	public String getDefaultResource() {
		return defaultResource;
	}

	public void setDefaultResource(String defaultResource) {
		this.defaultResource = defaultResource;
	}
}
