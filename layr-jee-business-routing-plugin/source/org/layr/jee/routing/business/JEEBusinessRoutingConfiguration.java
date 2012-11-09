package org.layr.jee.routing.business;

import java.util.Map;

import javax.servlet.ServletContext;

import org.layr.jee.commons.EnterpriseJavaBeans;
import org.layr.jee.commons.JEEConfiguration;

public class JEEBusinessRoutingConfiguration extends JEEConfiguration {

	private Map<String, Object> webResources;
	private EnterpriseJavaBeans ejbManager;
	
	public JEEBusinessRoutingConfiguration(ServletContext servletContext) {
		super(servletContext);
	}

	public Map<String, Object> getWebResources() {
		return webResources;
	}

	public void setWebResources(Map<String, Object> webResources) {
		this.webResources = webResources;
	}

	public EnterpriseJavaBeans getEjbManager() {
		return ejbManager;
	}

	public void setEjbManager(EnterpriseJavaBeans ejbManager) {
		this.ejbManager = ejbManager;
	}

	
}
