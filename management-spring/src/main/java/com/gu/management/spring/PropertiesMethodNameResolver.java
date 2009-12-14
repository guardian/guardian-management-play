package com.gu.management.spring;

import java.util.Properties;


public class PropertiesMethodNameResolver extends org.springframework.web.servlet.mvc.multiaction.PropertiesMethodNameResolver {

	private Properties mappings;

	public Properties getMappings() {
		return mappings;
	}

	@Override
	public void setMappings(Properties mappings) {
		this.mappings = mappings;
		super.setMappings(mappings);
	}
}
