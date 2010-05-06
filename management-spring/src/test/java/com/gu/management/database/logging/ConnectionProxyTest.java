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
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.MockUtil;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionProxyTest {
	private static final String SQL_QUERY = "select * from dual";

	@Mock Connection connectionMock;
	private ConnectionProxy connectionProxy;
	@Mock PreparedStatement actualPreparedStatement, preparedStatementWrapper;
    @Mock CallableStatement callableStatement, callableStatementWrapper;
	@Mock PreparedStatementProxyFactory preparedStatementProxyFactory;
    MockUtil mockUtil = new MockUtil();

	@Before
	public void setUp() throws Exception {
		connectionProxy = new ConnectionProxy(connectionMock, preparedStatementProxyFactory);

	}


    @Test
    public void shouldCreateAndReturnAPreparedStatementProxyWhenPrepareStatementIsCalled() throws Throwable {
        when(connectionMock.prepareStatement(SQL_QUERY)).thenReturn(actualPreparedStatement);
        when(preparedStatementProxyFactory.createPreparedStatementProxy(actualPreparedStatement, SQL_QUERY)).thenReturn(preparedStatementWrapper);
        connectionMock.prepareStatement(SQL_QUERY);
        Method executeMethod=methodExecutedOn(connectionMock);

        PreparedStatement returnedPreparedStatement = (PreparedStatement) connectionProxy.invoke(connectionMock, executeMethod, new Object[] { SQL_QUERY });
        assertThat(returnedPreparedStatement, sameInstance(preparedStatementWrapper));
    }

    @Test
    public void shouldCreateAndReturnAPreparedStatementProxyWhenCallableStatementIsCalled() throws Throwable {
        when(connectionMock.prepareCall(SQL_QUERY)).thenReturn(callableStatement);
        when(preparedStatementProxyFactory.createPreparedStatementProxy(callableStatement, SQL_QUERY)).thenReturn(callableStatementWrapper);
        connectionMock.prepareCall(SQL_QUERY);
        Method executeMethod=methodExecutedOn(connectionMock);

        CallableStatement returnedCallableStatement = (CallableStatement) connectionProxy.invoke(connectionMock, executeMethod, new Object[] { SQL_QUERY });
        assertThat(returnedCallableStatement, sameInstance(callableStatementWrapper));
    }

    @Test
    public void shouldNotCreateAProxyWhenNotCreatingAPreparedStatement() throws Throwable {
        connectionMock.isReadOnly();
        Method readOnlyMethod=methodExecutedOn(connectionMock);
        connectionProxy.invoke(connectionMock, readOnlyMethod, new Object[] {  });
        verifyZeroInteractions(preparedStatementProxyFactory);
    }


     private Method methodExecutedOn(Object mockObject) {
        return mockUtil.getMockHandler(mockObject).getRegisteredInvocations().get(0).getMethod();
    }

}