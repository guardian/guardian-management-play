package com.gu.management.database.logging;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.MockHandler;
import org.mockito.internal.util.MockUtil;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TimeableMethodPredicateTest {

    @Mock PreparedStatement preparedStatement;
    @Mock CallableStatement callableStatement;
    MockUtil mockUtil = new MockUtil();
   
    TimeableMethodPredicate timeable;

    @Before
    public void setUp(){
        timeable = new TimeableMethodPredicate();
    }

 	@Test
	public void shouldCallTimingMetricWithTimeSpentOnExecuteMethod() throws Throwable {
        preparedStatement.execute();

        Method executeMethod=methodExecutedOn(preparedStatement);

		assertThat(timeable.apply(executeMethod), equalTo(true));
	}

    @Test
	public void shouldCallTimingMetricWithTimeSpentOnExecuteUpdateMethod() throws Throwable {
        preparedStatement.executeUpdate();

        Method executeUpdateMethod=methodExecutedOn(preparedStatement);

		assertThat(timeable.apply(executeUpdateMethod), equalTo(true));
	}

	@Test
	public void shouldCallTimingMetricWithTimeSpentOnExecuteQueryMethod() throws Throwable {
		preparedStatement.executeQuery();

        Method executeQueryMethod=methodExecutedOn(preparedStatement);

		assertThat(timeable.apply(executeQueryMethod), equalTo(true));
	}

    @Test
	public void shouldCallTimingMetricWithTimeSpentOnCallableStatementExecuteMethod() throws Throwable {
        callableStatement.execute();

        Method executeMethod=methodExecutedOn(callableStatement);

		assertThat(timeable.apply(executeMethod), equalTo(true));
	}

    @Test
	public void shouldCallTimingMetricWithTimeSpentOnCallableStatementExecuteUpdateMethod() throws Throwable {
        callableStatement.executeUpdate();

        Method executeUpdateMethod=methodExecutedOn(callableStatement);

		assertThat(timeable.apply(executeUpdateMethod), equalTo(true));
	}

    private Method methodExecutedOn(Object mockObject) {
        return mockUtil.getMockHandler(mockObject).getRegisteredInvocations().get(0).getMethod();
    }
}
