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
		start();

		try {
			T value = callable.call();
			stop();
			return value;
		} catch (Exception e) {
			stopWatch.stop();
			log.warn(activity + " caused exception after " + stopWatch.getTime() + " ms", e);
			throw e;
		}
	}

	public  <T> T executeAndLogWithMetricUpdate(Callable<T> callable)  throws Exception {
		T result = executeAndLog(callable);
		metric.recordTimeSpent(getTime());
		return result;
	}
}