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