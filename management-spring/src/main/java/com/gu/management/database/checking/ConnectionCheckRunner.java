package com.gu.management.database.checking;


public class ConnectionCheckRunner implements Runnable {

	private final ConnectionChecker checker;
	private ConnectionCheckResult result;

	public ConnectionCheckRunner(ConnectionChecker checker) {
		this.checker = checker;
	}

	public void run() {
		result = checker.check();
	}

	public ConnectionCheckResult getResult() {
		return result;
	}

}