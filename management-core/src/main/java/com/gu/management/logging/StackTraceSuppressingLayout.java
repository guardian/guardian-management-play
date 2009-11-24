package com.gu.management.logging;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

public class StackTraceSuppressingLayout extends Layout {

    private Layout wrappedLayout;

    public StackTraceSuppressingLayout(Layout wrappedLayout) {
        this.wrappedLayout = wrappedLayout;
    }

    public String format(LoggingEvent event) {
        String messageFromWrappedLayout = wrappedLayout.format(event);
        if (event.getThrowableInformation() != null && wrappedLayout.ignoresThrowable()) {
            return messageFromWrappedLayout
                + AbbreviatedExceptionFormatter.format(event.getThrowableInformation().getThrowable()) + "\n";
        } else {
            return messageFromWrappedLayout;
        }
    }

    public String getContentType() {
        return wrappedLayout.getContentType();
    }

    public String getHeader() {
        return wrappedLayout.getHeader();
    }

    public String getFooter() {
        return wrappedLayout.getFooter();
    }

    // this surpresses the default stack output behaviour
    public boolean ignoresThrowable() {
        return false;
    }

    public void activateOptions() {
        wrappedLayout.activateOptions();
    }

    public Layout getWrappedLayout() {
        return wrappedLayout;
    }
}
