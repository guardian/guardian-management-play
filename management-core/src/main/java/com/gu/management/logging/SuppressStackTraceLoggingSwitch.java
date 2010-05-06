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

import com.gu.management.switching.Switchable;
import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Enumeration;

public class SuppressStackTraceLoggingSwitch implements Switchable {

    private final static Logger LOGGER = LogManager.getLogger(SuppressStackTraceLoggingSwitch.class);

    // NB by default we want stack trace suppression on.
    // The internal state is set to
    // off initially as this reflects log4j's state on startup, the constuctor is
    // responsible for turning suppression on and updating log4j's state.
    private boolean isOn = false;

    public SuppressStackTraceLoggingSwitch() {
        switchOn();
    }

    @Override
    public boolean isSwitchedOn() {
        return isOn;
    }

    @Override
    public synchronized void switchOff() {
        if (isOn) {
            Enumeration appenders = LogManager.getRootLogger().getAllAppenders();
            while (appenders.hasMoreElements()) {
                Appender appender = (Appender) appenders.nextElement();
                if (appender.getLayout() instanceof StackTraceSuppressingLayout) {
                    StackTraceSuppressingLayout surpressingLayout = (StackTraceSuppressingLayout) appender.getLayout();
                    appender.setLayout((surpressingLayout.getWrappedLayout()));
                }
            }
            LOGGER.debug("Switched to full stacktrace mode.");
        }
        isOn = false;
    }

    @Override
    public synchronized void switchOn() {
        if (!isOn) {
            Enumeration appenders = LogManager.getRootLogger().getAllAppenders();
            while (appenders.hasMoreElements()) {
                Appender appender = (Appender) appenders.nextElement();
                appender.setLayout(new StackTraceSuppressingLayout(appender.getLayout()));
            }
            LOGGER.debug("Switched to suppressed stacktrace mode.");
        }
        isOn = true;
    }

    @Override
    public String getDescription() {
        return "Suppresses full stack traces in log4j, when switched on only exception messages are displayed";
    }

    @Override
    public String getWordForUrl() {
        return "suppress-stack-trace";
    }
}
