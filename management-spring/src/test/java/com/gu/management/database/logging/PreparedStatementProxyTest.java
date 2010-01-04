package com.gu.management.database.logging;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.gu.management.timing.TimingMetric;

@RunWith(MockitoJUnitRunner.class)
public class PreparedStatementProxyTest {

	private static final String SQL_QUERY = "select * from dual";

	private PreparedStatementProxy preparedStatementProxy;
	@Mock PreparedStatement preparedStatementMock;
	@Mock TimingMetric metricMock;
    @Mock TimeableMethodPredicate timeableMethodPredicate;

	@Before
	public void setUp() throws Exception {
		preparedStatementProxy = new PreparedStatementProxy(preparedStatementMock, SQL_QUERY, metricMock, timeableMethodPredicate);
	}

    @Test(expected = SQLException.class)
    public void shouldThrowUnderlyingExceptionRatherThanTheInvocationTargetExceptionYouGetFromInvokingAMethodWithReflection() throws Throwable {
        Method methodThatThrowsException = PreparedStatement.class.getMethod("isClosed");
        when(preparedStatementMock.isClosed()).thenThrow(new SQLException("AARGH!"));

        preparedStatementProxy.invoke(null, methodThatThrowsException, new Object[] {});
    }

    @Test(expected = SQLException.class)
    public void shouldThrowUnderlyingExceptionRatherThanTheInvocationTargetExceptionYouGetFromInvokingAMethodWithReflectionWhenTiming() throws Throwable {
        Method methodThatThrowsException = PreparedStatement.class.getMethod("isClosed");
        when(preparedStatementMock.isClosed()).thenThrow(new SQLException("AARGH!"));
        when(timeableMethodPredicate.apply(methodThatThrowsException)).thenReturn(true);
        preparedStatementProxy.invoke(null, methodThatThrowsException, new Object[] {});
    }

	@Test
	public void shouldCallTimingMetricIfPredicateSaysToApplyTiming() throws Throwable {
        Method methodToTime = Object.class.getDeclaredMethod("toString");
        
        when(timeableMethodPredicate.apply(methodToTime)).thenReturn(true);

		preparedStatementProxy.invoke(null, methodToTime, new Object[] {});

        verify(metricMock).recordTimeSpent(anyInt());
	}

    @Test
	public void shouldNotCallTimingMetricIfPredicateSaysNotToApplyTiming() throws Throwable {
        Method methodToNotTime = Object.class.getDeclaredMethod("toString");

        when(timeableMethodPredicate.apply(methodToNotTime)).thenReturn(false);

		preparedStatementProxy.invoke(null, methodToNotTime, new Object[] {});

        verifyZeroInteractions(metricMock);
	}

}