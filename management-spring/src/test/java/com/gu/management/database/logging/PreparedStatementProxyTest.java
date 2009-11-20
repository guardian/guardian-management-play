package com.gu.management.database.logging;

import com.gu.management.timing.TimingMetric;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.PreparedStatement;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PreparedStatementProxyTest {

	private static final String SQL_QUERY = "select * from dual";

	private PreparedStatementProxy preparedStatementProxy;
	@Mock PreparedStatement preparedStatementMock;
	@Mock TimingMetric metricMock;

	@Before
	public void setUp() throws Exception {
		preparedStatementProxy = new PreparedStatementProxy(preparedStatementMock, SQL_QUERY, metricMock);
	}

	@Test
	public void shouldCallTimingMetricWithTimeSpentOnExecuteMethod() throws Throwable {
		preparedStatementProxy.invoke(null, PreparedStatementProxy.EXECUTE_METHOD, new Object[] {});

        verify(metricMock).recordTimeSpent(anyInt());
	}

	@Test
	public void shouldCallTimingMetricWithTimeSpentOnExecuteUpdateMethod() throws Throwable {
		preparedStatementProxy.invoke(null, PreparedStatementProxy.EXECUTE_UPDATE_METHOD, new Object[] {});

        verify(metricMock).recordTimeSpent(anyInt());
	}

	@Test
	public void shouldCallTimingMetricWithTimeSpentOnExecuteQueryMethod() throws Throwable {
		preparedStatementProxy.invoke(null, PreparedStatementProxy.EXECUTE_QUERY_METHOD, new Object[] {});

        verify(metricMock).recordTimeSpent(anyInt());
	}
}