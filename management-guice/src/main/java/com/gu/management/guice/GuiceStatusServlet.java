package com.gu.management.guice;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.gu.management.status.StatusServlet;
import com.gu.management.status.StatusWriter;

import java.util.Collection;

@Singleton
public class GuiceStatusServlet extends StatusServlet{
    @Inject
    public GuiceStatusServlet(@Named("StatusServletWriters")Collection<StatusWriter> statusWriters) {
        super(statusWriters);
    }
}
