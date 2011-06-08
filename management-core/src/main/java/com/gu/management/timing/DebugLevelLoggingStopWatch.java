package com.gu.management.timing;

import org.slf4j.Logger;

public class DebugLevelLoggingStopWatch extends LoggingStopWatch {
	public DebugLevelLoggingStopWatch(Logger log, String activity) {
		super(log, activity);
	}

	public DebugLevelLoggingStopWatch(Logger log, String activity, TimingMetric metric) {
		super(log, activity, metric);
	}

	@Override
	protected boolean shouldLogComplete() {
		return log.isDebugEnabled();
	}

	@Override
	protected void logComplete(String msg) {
		log.debug(msg);
	}
}
