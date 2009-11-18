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