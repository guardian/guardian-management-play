package com.gu.management.database.logging;

import com.gu.management.timing.LoggingStopWatch;
import com.gu.management.timing.TimingMetric;
import net.sf.cglib.proxy.InvocationHandler;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.util.concurrent.Callable;

class PreparedStatementProxy implements InvocationHandler {

	static final Method EXECUTE_QUERY_METHOD;
	static final Method EXECUTE_METHOD;
	static final Method EXECUTE_UPDATE;

	private static final Logger LOG = Logger.getLogger(PreparedStatementProxy.class);

	private final PreparedStatement targetStatement;
	private final String sqlQuery;

	private String sqlComment;
	private final TimingMetric metric;

	static {
		try {
			EXECUTE_METHOD = PreparedStatement.class.getDeclaredMethod("execute", new Class[] {});
			EXECUTE_QUERY_METHOD = PreparedStatement.class.getDeclaredMethod("executeQuery", new Class[] {});
			EXECUTE_UPDATE = PreparedStatement.class.getDeclaredMethod("executeUpdate", new Class[] {});
		} catch (SecurityException e) {
			throw new RuntimeException("Cannot reflect into class", e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Cannot find method", e);
		}
	}

	PreparedStatementProxy(PreparedStatement targetStatement, String sqlQuery,
	                       TimingMetric metric) {
		this.targetStatement = targetStatement;
		this.sqlQuery = sqlQuery;
		this.metric = metric;
	}

	@Override
	public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
		if ((EXECUTE_METHOD.equals(method) || EXECUTE_QUERY_METHOD.equals(method) || EXECUTE_UPDATE.equals(method))) {

			LoggingStopWatch loggingStopWatch = new LoggingStopWatch(LOG, "Query " + getQueryDisplayName(), Level.DEBUG);

			Object result = loggingStopWatch.executeAndLog(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					return method.invoke(targetStatement, args);
				}
			});

			metric.recordTimeSpent(loggingStopWatch.getTime());
			return result;
		}

		return method.invoke(targetStatement, args);
	}

	private String getQueryDisplayName() {
		return "\"" + getSqlComment() + "\"";
	}

	private String getSqlComment() {
		if (sqlComment == null) {
			sqlComment = new SqlQueryData(sqlQuery).getComment();
		}
		return sqlComment;
	}

}