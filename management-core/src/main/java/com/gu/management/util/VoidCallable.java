package com.gu.management.util;

import java.util.concurrent.Callable;

public abstract class VoidCallable implements Callable<Object> {
	public Object call() throws Exception {
		voidCall();
		return null;
	}

	protected abstract void voidCall() throws Exception;
}