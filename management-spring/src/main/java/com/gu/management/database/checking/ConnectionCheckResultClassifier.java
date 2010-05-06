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

import com.google.common.collect.ImmutableList;
import org.hibernate.JDBCException;

import java.util.Collection;
import java.util.List;

public class ConnectionCheckResultClassifier {

	private final Collection<Integer> severeErrorCodes;
	private final Collection<String> severeErrorMessages;
	private final List<Class<? extends Exception>> severeExceptionClasses = ImmutableList.<Class<? extends Exception>>of(java.util.concurrent.TimeoutException.class);

	public ConnectionCheckResultClassifier(Collection<Integer> severeErrorCodes, Collection<String> severeErrorMessages) {
		this.severeErrorCodes = severeErrorCodes;
		this.severeErrorMessages = severeErrorMessages;
	}

	public boolean isSevere(ConnectionCheckResult result) {
		if (result.isSuccessful()) {
			return false;
		}

		Exception failureCause = result.getFailureCause();
		if (failureCause instanceof JDBCException) {
			JDBCException jdbcFailureCause = (JDBCException) failureCause;
			if (containsSevereErrorCodeOrMessage(jdbcFailureCause)) {
				return true;
			}
		} else if (matchesSevereExceptionClass(failureCause)) {
			return true;
		}
		return false;
	}

	private boolean containsSevereErrorCodeOrMessage(JDBCException jdbcFailureCause) {
		if (severeErrorCodes.contains(jdbcFailureCause.getErrorCode())) {
			return true;
		}
		if (containsSevereErrorMessage(jdbcFailureCause)) {
			return true;
		}
		return false;
	}

	private boolean containsSevereErrorMessage(JDBCException jdbcFailureCause) {
		for (String severeErrorMessage : severeErrorMessages) {
			if (jdbcFailureCause.getSQLException().getMessage().contains(severeErrorMessage)) {
				return true;
			}
		}
		return false;
	}

	protected boolean matchesSevereExceptionClass(Exception failureCause) {
		for (Class<? extends Exception> severeExceptionClass : severeExceptionClasses) {
			if (severeExceptionClass.isAssignableFrom(failureCause.getClass())) {
				return true;
			}
		}
		return false;
	}
}