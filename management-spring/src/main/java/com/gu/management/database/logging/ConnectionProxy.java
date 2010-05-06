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

import static com.google.common.collect.Sets.newHashSet;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Set;

import net.sf.cglib.proxy.InvocationHandler;


class ConnectionProxy implements InvocationHandler {

	private final Connection targetConnection;
    private final PreparedStatementProxyFactory preparedStatementProxyFactory;
    private final Set<String> methodNames = newHashSet("prepareStatement", "prepareCall");



	ConnectionProxy(Connection targetConnection, PreparedStatementProxyFactory preparedStatementProxyFactory) {
		this.targetConnection = targetConnection;
        this.preparedStatementProxyFactory = preparedStatementProxyFactory;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (methodNames.contains(method.getName())) {
			String sqlQuery = (String) args[0];
            PreparedStatement preparedStatement = (PreparedStatement) method.invoke(targetConnection, args);
            return preparedStatementProxyFactory.createPreparedStatementProxy(preparedStatement, sqlQuery);
		}

		return method.invoke(targetConnection, args);
	}

	

}