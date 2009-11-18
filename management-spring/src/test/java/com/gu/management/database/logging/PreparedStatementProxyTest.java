package com.gu.management.database.logging;

import com.gu.management.timing.TimingMetric;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.PreparedStatement;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

public class PreparedStatementProxyTest {

	private static final String SQL_QUERY = "select * from dual";

	private PreparedStatementProxy preparedStatementProxy;
	@Mock PreparedStatement preparedStatementMock;
	@Mock
    TimingMetric metricMock;

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);

		preparedStatementProxy = new PreparedStatementProxy(preparedStatementMock, SQL_QUERY,
				metricMock);
	}

	@Test
	public void testNotifiesOfSqlExecutedWhenExecuteMethodCalled() throws Throwable {
		when(preparedStatementMock.execute()).thenReturn(true);
		metricMock.recordTimeSpent(anyInt());

		preparedStatementProxy.invoke(null, PreparedStatementProxy.EXECUTE_METHOD, new Object[] {});
	}

	@Test
	public void testShouldNotifyMetricWhenExecuteMethodCalled() throws Throwable {
		when(preparedStatementMock.execute()).thenReturn(true);
		metricMock.recordTimeSpent(anyInt());

		preparedStatementProxy.invoke(null, PreparedStatementProxy.EXECUTE_METHOD, new Object[] {});
	}

	@Test
	public void testShouldNotifyMetricWhenExecuteUpdateMethodCalled() throws Throwable {
		when(preparedStatementMock.executeUpdate()).thenReturn(1);

		metricMock.recordTimeSpent(anyInt());

		preparedStatementProxy.invoke(null, PreparedStatementProxy.EXECUTE_UPDATE, new Object[] {});
	}

	@Test
	public void testShouldNotifyMetricWhenExecuteQueryMethodCalled() throws Throwable {
		when(preparedStatementMock.executeQuery()).thenReturn(null);

		metricMock.recordTimeSpent(anyInt());

		preparedStatementProxy.invoke(null, PreparedStatementProxy.EXECUTE_QUERY_METHOD, new Object[] {});
	}
}