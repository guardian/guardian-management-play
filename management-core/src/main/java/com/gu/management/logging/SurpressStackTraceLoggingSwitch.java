package com.gu.management.logging;

import com.gu.management.switching.Switchable;
import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Enumeration;

public class SurpressStackTraceLoggingSwitch implements Switchable {

    private final static Logger LOGGER = LogManager.getLogger(SurpressStackTraceLoggingSwitch.class);

    // NB by default we want stack trace surpression on.
    // The internal state is set to
    // off initially as this reflects log4j's state on startup, the constuctor is
    // responsible for turning supression on and updating log4j's state.
    private boolean isOn = false;

    public SurpressStackTraceLoggingSwitch() {
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
                if (appender.getLayout() instanceof StackTraceSurpressingLayout) {
                    StackTraceSurpressingLayout surpressingLayout = (StackTraceSurpressingLayout) appender.getLayout();
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
                appender.setLayout(new StackTraceSurpressingLayout(appender.getLayout()));
            }
            LOGGER.debug("Switched to supressed stacktrace mode.");
        }
        isOn = true;
    }

    @Override
    public String getDescription() {
        return "Surpresses full stack traces in log4j, when switched on only exception messages are displayed";
    }

    @Override
    public String getWordForUrl() {
        return "surpress-stack-trace";
    }
}