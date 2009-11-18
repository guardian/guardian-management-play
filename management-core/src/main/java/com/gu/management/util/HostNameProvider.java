package com.gu.management.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostNameProvider {

	private static final String HOSTNAME_TO_USE_WHEN_DNS_IS_NOT_WORKING = "localhost";

    public String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName().toLowerCase();
		} catch (UnknownHostException e) {
			return HOSTNAME_TO_USE_WHEN_DNS_IS_NOT_WORKING;
		}
	}

	public String getFullQualifiedHostName() {
		try {
			return InetAddress.getLocalHost().getCanonicalHostName().toLowerCase();
		} catch (UnknownHostException e) {
			return HOSTNAME_TO_USE_WHEN_DNS_IS_NOT_WORKING;
		}
	}
}