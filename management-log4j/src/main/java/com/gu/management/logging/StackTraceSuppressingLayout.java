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
