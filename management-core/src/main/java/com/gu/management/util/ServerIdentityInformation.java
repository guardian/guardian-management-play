package com.gu.management.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerIdentityInformation {

	private HostNameProvider hostNameProvider = new HostNameProvider();
	private String hostName;

	public String getPublicHostIdentifier() {
		String returnValue = getHostName();

		if (returnValue.length() > 2) {
			return returnValue.substring(returnValue.length() - 2);
		}

		return returnValue;
	}

	private String getHostName() {
		if (hostName == null) {
			hostName = hostNameProvider.getHostName();
		}

		return hostName;

	}

	public void setHostNameProvider(HostNameProvider hostNameProvider) {
		this.hostNameProvider = hostNameProvider;
	}

	public String getAsHtmlComment() {
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS").format(new Date());
		return String.format("<!--[if !IE]> GUERR (%s) %s <![endif]-->", getPublicHostIdentifier(), date);
	}
}