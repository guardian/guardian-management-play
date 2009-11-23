package com.gu.management.database.logging;

import com.google.common.collect.Sets;
import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;


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