package com.gu.management.timing;

public class NullMetric extends TimingMetric {

	@Override public long getCount() {
		return 0;
	}

	@Override public long getTotalTimeInMillis() {
		return 0;
	}

	@Override public void recordTimeSpent(long durationInMillis) {
		// Do nothing
	}

}