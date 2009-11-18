package com.gu.management.database.logging;

import com.gu.management.timing.TimingMetric;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

public class ConnectionProxyTest {
	private static final String SQL_QUERY = "select * from dual";
	private Method prepareSqlMethod;

	@Mock Connection connectionMock;
	private ConnectionProxy connectionProxy;
	@Mock PreparedStatement preparedStatementMock;
	@Mock
    TimingMetric metricMock;


	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		connectionProxy = new ConnectionProxy(connectionMock, metricMock);
		prepareSqlMethod = Connection.class.getMethod("prepareStatement", new Class[] { String.class });
	}

	@Test
	public void testInvokePassesThroughOnUnproxiedMethod() throws Throwable {
		Method commitMethod = Connection.class.getMethod("commit", new Class[] {});
		connectionMock.commit();
		connectionProxy.invoke(connectionMock, commitMethod, new Object[] {});
	}

	@Test
	public void testPassesPrepareStamentCallsThroughToSpecializedProxy() throws Throwable {
		when(connectionMock.prepareStatement(SQL_QUERY)).thenReturn(preparedStatementMock);

		connectionProxy.invoke(connectionMock, prepareSqlMethod, new Object[] { SQL_QUERY });
	}

	@Test
	public void testCreatedProxyShouldInvokeMetric() throws Throwable {
		when(connectionMock.prepareStatement(SQL_QUERY)).thenReturn(preparedStatementMock);
		when(preparedStatementMock.execute()).thenReturn(true);
		metricMock.recordTimeSpent(anyInt());

		PreparedStatement preparedStatement = (PreparedStatement) connectionProxy.invoke(null, prepareSqlMethod, new Object[] { SQL_QUERY });
		preparedStatement.execute();
	}
}