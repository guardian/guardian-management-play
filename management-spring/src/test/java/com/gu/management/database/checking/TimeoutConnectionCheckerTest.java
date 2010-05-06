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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(MockitoJUnitRunner.class)
public class TimeoutConnectionCheckerTest {

	@Mock ConnectionCheckRunner runner;

	@Test
	public void testShouldExecuteConnectionCheckRunnerWithTimeoutController() throws Exception {
		TimeoutConnectionChecker checker = new TimeoutConnectionChecker(runner);

		runner.run();
		checker.check();
	}

	@Test
	public void testShouldReturnResultOfConnectionCheckRunner() throws Exception {
		ConnectionCheckResult result = new ConnectionCheckResult(true);
		ConnectionCheckRunner runner = new SuccessfulConnectionCheckRunner(result);

		TimeoutConnectionChecker checker = new TimeoutConnectionChecker(runner);

		ConnectionCheckResult actualResult = checker.check();
		assertThat(actualResult, sameInstance(result));
	}

	@Test
	public void testShouldReturnResultWithTimeoutExceptionWhenCheckTimesout() throws Exception {
		ConnectionCheckRunner runner = new TimeoutConnectionCheckRunner(15000);

		TimeoutConnectionChecker checker = new TimeoutConnectionChecker(runner);
		checker.setTimeout(1);

		ConnectionCheckResult result = checker.check();

		assertThat(result.isFailure(), equalTo(true));
		assertThat(result.getFailureCause(), is(TimeoutException.class));
	}

	private static class SuccessfulConnectionCheckRunner extends ConnectionCheckRunner {

		private final ConnectionCheckResult result;

		public SuccessfulConnectionCheckRunner(ConnectionCheckResult result) {
			super(null);
			this.result = result;
		}

		@Override
		public ConnectionCheckResult getResult() {
			return result;
		}

	}

	private static class TimeoutConnectionCheckRunner extends ConnectionCheckRunner {

		private final long waitInMilliseconds;

		public TimeoutConnectionCheckRunner(long waitInMilliseconds) {
			super(null);
			this.waitInMilliseconds = waitInMilliseconds;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(waitInMilliseconds);
			} catch (InterruptedException exception) {
				// Do nothing
			}
		}
	}
}