package com.gu.management.database.logging;

import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.Proxy;

public abstract class ProxyHelper {

	private ProxyHelper() {
	}

	@SuppressWarnings( { "unchecked" })
	public static <T> T proxy(T objectToProxy, InvocationHandler handler) {
		Class<?> targetClass = objectToProxy.getClass();
		return (T) Proxy.newProxyInstance(targetClass.getClassLoader(), targetClass.getInterfaces(), handler);
	}

}