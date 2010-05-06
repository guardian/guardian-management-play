/*
 * Copyright 2010 Guardian News and Media
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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