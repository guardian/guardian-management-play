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

import com.gu.management.timing.LoggingStopWatch;
import com.gu.management.timing.TimingMetric;
import net.sf.cglib.proxy.InvocationHandler;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
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
					return invokeMethodAndThrowAnyUnderlyingException(method, args);
				}
			});

			metric.recordTimeSpent(loggingStopWatch.getTime());
			return result;
		}

        return invokeMethodAndThrowAnyUnderlyingException(method, args);
    }

    private Object invokeMethodAndThrowAnyUnderlyingException(Method method, Object[] args) throws Exception {
        try {
            return method.invoke(targetStatement, args);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Exception) {
                throw (Exception) cause;
            }
            throw e;
        }
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