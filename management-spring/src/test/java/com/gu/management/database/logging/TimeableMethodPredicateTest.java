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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.MockUtil;
import org.mockito.runners.MockitoJUnitRunner;

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
