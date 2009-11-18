package com.gu.management.database.checking;


public class ConnectionCheckResult {

	private final boolean successful;
	private Exception failureCause;

	public ConnectionCheckResult(boolean successful) {
		this.successful = successful;
	}

	public ConnectionCheckResult(Exception exception) {
		this.successful = false;
		this.failureCause = exception;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public Exception getFailureCause() {
		return failureCause;
	}

	public boolean isFailure() {
		return !isSuccessful();
	}

}