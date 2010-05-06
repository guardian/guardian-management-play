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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ConnectionCheckRunnerTest {
	@Mock SimpleConnectionChecker checker;

	@Test
	public void testShouldCheckConnectionOnRun() throws Exception {
		ConnectionCheckRunner runner = new ConnectionCheckRunner(checker);
		when(checker.check()).thenReturn(null);
		runner.run();
	}

	@Test
	public void testShouldRetrieveResultOfConnectionCheckAfterRun() throws Exception {
		ConnectionCheckResult result = new ConnectionCheckResult(true);
		ConnectionChecker checker = new ConnectionCheckerStub(result);

		ConnectionCheckRunner runner = new ConnectionCheckRunner(checker);

		runner.run();

		assertThat(runner.getResult(), equalTo((result)));
	}

	private static class ConnectionCheckerStub implements ConnectionChecker {
		private final ConnectionCheckResult result;

		public ConnectionCheckerStub(ConnectionCheckResult result) {
			this.result = result;
		}

		public ConnectionCheckResult check() {
			return result;
		}
	}
}