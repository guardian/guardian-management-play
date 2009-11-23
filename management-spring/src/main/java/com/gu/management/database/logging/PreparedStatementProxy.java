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


	private static final Logger LOG = Logger.getLogger(PreparedStatementProxy.class);

	private final PreparedStatement targetStatement;
	private final String sqlQuery;

	private String sqlComment;
	private final TimingMetric metric;
    private final TimeableMethodPredicate timeableMethodPredicate;

	PreparedStatementProxy(PreparedStatement targetStatement, String sqlQuery,
                           TimingMetric metric, TimeableMethodPredicate timeableMethodPredicate) {
		this.targetStatement = targetStatement;
		this.sqlQuery = sqlQuery;
		this.metric = metric;
        this.timeableMethodPredicate = timeableMethodPredicate;
	}

	@Override
	public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
		if (timeableMethodPredicate.apply(method)) {

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