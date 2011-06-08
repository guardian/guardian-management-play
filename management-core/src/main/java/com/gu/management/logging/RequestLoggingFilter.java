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

package com.gu.management.logging;

import com.google.common.collect.ImmutableSet;
import com.gu.management.timing.TimingMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;

public class RequestLoggingFilter extends ConfigurableLoggingFilter {
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @SuppressWarnings("unused")
    public RequestLoggingFilter(TimingMetric timingMetric) {
        super(timingMetric);
    }

    public RequestLoggingFilter() {
        super();
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected boolean shouldLogParametersOnNonGetRequests() {
        return false;
    }

    @Override
    protected Set<String> parametersToSuppressInLogs() {
        return Collections.emptySet();
    }

    @Override
    protected Set<String> pathPrefixesToLogAtTrace() {
        return ImmutableSet.of("/management", "/status");
    }
}