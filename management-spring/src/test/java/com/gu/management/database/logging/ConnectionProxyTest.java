package com.gu.management.database.logging;

import com.gu.management.timing.TimingMetric;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionProxyTest {
	private static final String SQL_QUERY = "select * from dual";
	private Method prepareSqlMethod;
    private Method isReadOnlyMethod;

	@Mock Connection connectionMock;
	private ConnectionProxy connectionProxy;
	@Mock PreparedStatement actualPreparedStatement, preparedStatementWrapper;
	@Mock PreparedStatementProxyFactory preparedStatementProxyFactory;


	@Before
	public void setUp() throws Exception {
		connectionProxy = new ConnectionProxy(connectionMock, preparedStatementProxyFactory);
		prepareSqlMethod = Connection.class.getMethod("prepareStatement", new Class[] { String.class });
        isReadOnlyMethod = Connection.class.getMethod("isReadOnly", new Class[] { });
	}


    @Test
    public void shouldCreateAndReturnAPreparedStatementProxyWhenPrepareStatementIsCalled() throws Throwable {
        when(connectionMock.prepareStatement(SQL_QUERY)).thenReturn(actualPreparedStatement);
        when(preparedStatementProxyFactory.createPreparedStatementProxy(actualPreparedStatement, SQL_QUERY)).thenReturn(preparedStatementWrapper);

        PreparedStatement returnedPreparedStatement = (PreparedStatement) connectionProxy.invoke(connectionMock, prepareSqlMethod, new Object[] { SQL_QUERY });
        assertThat(returnedPreparedStatement, sameInstance(preparedStatementWrapper));
    }

    @Test
    public void shouldNotCreateAProxyWhenNotCreatingAPreparedStatement() throws Throwable {
        connectionProxy.invoke(connectionMock, isReadOnlyMethod, new Object[] {  });
        verifyZeroInteractions(preparedStatementProxyFactory);
    }


}