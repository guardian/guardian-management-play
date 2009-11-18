package com.gu.management.database.logging;

import com.gu.management.timing.TimingMetric;
import net.sf.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;


class ConnectionProxy implements InvocationHandler {

	protected static final Method PREPARE_STATEMENT_METHOD;
	private final Connection targetConnection;
	private final TimingMetric metric;

	static {
		try {
			PREPARE_STATEMENT_METHOD = Connection.class.getDeclaredMethod("prepareStatement", new Class[] { String.class });
		} catch (SecurityException e) {
			throw new RuntimeException("Cannot reflect into class", e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Cannot find method", e);
		}
	}

	ConnectionProxy(Connection targetConnection, TimingMetric metric) {
		this.targetConnection = targetConnection;
		this.metric = metric;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (PREPARE_STATEMENT_METHOD.equals(method)) {
			String sqlQuery = (String) args[0];
			return createPreparedStatementProxy((PreparedStatement) method.invoke(targetConnection, args), sqlQuery);
		}

		return method.invoke(targetConnection, args);
	}

	private PreparedStatement createPreparedStatementProxy(PreparedStatement targetStatement, String sqlQuery) {
		return ProxyHelper.proxy(targetStatement, new PreparedStatementProxy(targetStatement, sqlQuery,
				metric));
	}

}