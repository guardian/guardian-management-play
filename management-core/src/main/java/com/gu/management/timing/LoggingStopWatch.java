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

package com.gu.management.timing;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.concurrent.Callable;

public class LoggingStopWatch {

	protected final Logger log;
	protected final String activity;

	private final Level level;
	private final TimingMetric metric;

	private final StopWatch stopWatch = new StopWatch();

	public LoggingStopWatch(Logger log, String activity) {
		this(log, activity, Level.INFO, new NullMetric());
	}

	public LoggingStopWatch(Logger log, String activity, Level level) {
		this(log, activity, level, new NullMetric());
	}

	public LoggingStopWatch(Logger log, String activity, TimingMetric metric) {
		this(log, activity, Level.INFO, metric);
	}

	public LoggingStopWatch(Logger log, String activity, Level level, TimingMetric metric) {
		this.log = log;
		this.activity = activity;
		this.level = level;
		this.metric = metric;
	}

	public void start() {
		if (log.isTraceEnabled()) {
			log.trace(activity);
		}
		stopWatch.start();
	}

	public void stop() {
		stopWatch.stop();
		if (log.isEnabledFor(level)) {
			log.log(level, activity + " completed in " + stopWatch.getTime() + " ms");
		}
	}

	public long getTime() {
		return stopWatch.getTime();
	}

	public <T> T executeAndLog(Callable<T> callable) throws Exception {
        try {
            return executeAndTime(callable);
		} catch (Exception e) {
            handleCallableException(e);
			throw e;
		}
	}

    public <T> T executeAndLogUnchecked(Callable<T> callable) {
		try {
            return executeAndTime(callable);
		} catch (Exception e) {
            handleCallableException(e);
			throw new RuntimeException(e);
		}
	}

    private <T> void handleCallableException(Exception e) {
        stopWatch.stop();
        log.warn(activity + " caused exception after " + stopWatch.getTime() + " ms", e);
    }

    private <T> T executeAndTime(Callable<T> callable) throws Exception {
        start();
        T value = callable.call();
        stop();
        return value;
    }


	public  <T> T executeAndLogWithMetricUpdate(Callable<T> callable)  throws Exception {
		T result = executeAndLog(callable);
		metric.recordTimeSpent(getTime());
		return result;
	}
}