package com.gu.management.logging;

import org.apache.log4j.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SurpressStackTraceLoggingSwitchTest {

    private Map<Appender, Layout> orignalLayoutState = new HashMap<Appender, Layout>();
    private ConsoleAppender testAppender;
    private SurpressStackTraceLoggingSwitch theSwitch;

    @Before
    public void getOriginalLoggerLayouts() {

        testAppender = new ConsoleAppender(new SimpleLayout());
        LogManager.getRootLogger().addAppender(testAppender);

        Enumeration appenders = LogManager.getRootLogger().getAllAppenders();

        while (appenders.hasMoreElements()) {
            Appender appender = (Appender) appenders.nextElement();
            orignalLayoutState.put(appender, appender.getLayout());
        }

        theSwitch = new SurpressStackTraceLoggingSwitch();
    }

    @After
    public void resetLayoutState() {
        for (Appender appender : orignalLayoutState.keySet()) {
            appender.setLayout(orignalLayoutState.get(appender));
        }
        LogManager.getRootLogger().removeAppender(testAppender);
    }

    @Test
    public void shouldWrapStackTraceSurpressLayoutAroundExistingLayoutWhenSwitchedOn() {

        theSwitch.switchOn();
        for (Appender appender : orignalLayoutState.keySet()) {
            assertThat(appender.getLayout(), instanceOf(StackTraceSurpressingLayout.class));
            StackTraceSurpressingLayout layout = (StackTraceSurpressingLayout) appender.getLayout();
            assertThat(layout.getWrappedLayout(), equalTo(orignalLayoutState.get(appender)));
        }

        assertTrue(theSwitch.isSwitchedOn());
    }

    @Test
    public void secondCallToSwitchOnShouldDoNothing() {
        theSwitch.switchOn();
        assertTrue(theSwitch.isSwitchedOn());

        theSwitch.switchOn();
        assertTrue(theSwitch.isSwitchedOn());

        for (Appender appender : orignalLayoutState.keySet()) {
            assertThat(appender.getLayout(), instanceOf(StackTraceSurpressingLayout.class));
            StackTraceSurpressingLayout layout = (StackTraceSurpressingLayout) appender.getLayout();
            assertThat(layout.getWrappedLayout(), equalTo(orignalLayoutState.get(appender)));
        }
    }

    @Test
    public void shouldUnWrapLayoutsWhenSwitchingOff() {

        theSwitch.switchOn();
        for (Appender appender : orignalLayoutState.keySet()) {
            assertThat(appender.getLayout(), instanceOf(StackTraceSurpressingLayout.class));
            StackTraceSurpressingLayout layout = (StackTraceSurpressingLayout) appender.getLayout();
            assertThat(layout.getWrappedLayout(), equalTo(orignalLayoutState.get(appender)));
        }

        theSwitch.switchOff();
        for (Appender appender : orignalLayoutState.keySet()) {
            assertThat(appender.getLayout(), equalTo(orignalLayoutState.get(appender)));
        }
    }

}
