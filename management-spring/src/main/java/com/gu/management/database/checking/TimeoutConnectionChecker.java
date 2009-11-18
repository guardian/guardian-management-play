package com.gu.management.database.checking;

import org.apache.commons.httpclient.util.TimeoutController;
import org.apache.commons.httpclient.util.TimeoutController.TimeoutException;
import org.apache.log4j.Logger;


public class TimeoutConnectionChecker implements ConnectionChecker {

	private static final Logger LOGGER = Logger.getLogger(TimeoutConnectionChecker.class);

	private int timeout;
	private final ConnectionCheckRunner runner;

	public TimeoutConnectionChecker(ConnectionCheckRunner runner) {
		this.runner = runner;
	}

	public ConnectionCheckResult check() {
		try {
			TimeoutController.execute(runner, timeout);
			return runner.getResult();
		} catch (TimeoutException e) {
			LOGGER.debug("Got timeout exceeding limit of "+timeout+"ms checking database connection", e);
			return new ConnectionCheckResult( new java.util.concurrent.TimeoutException(e.getMessage()));
		}
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

}