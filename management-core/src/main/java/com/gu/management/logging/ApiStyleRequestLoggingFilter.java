package com.gu.management.logging;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

@SuppressWarnings("unused")
public class ApiStyleRequestLoggingFilter extends RequestLoggingFilter {

    private final Set<String> parametersToSuppressInLogs = ImmutableSet.of("password");

    @Override
    protected boolean shouldLogParametersOnNonGetRequests() {
        return true;
    }

    @Override
    protected Set<String> parametersToSuppressInLogs() {
        return parametersToSuppressInLogs;
    }
}
