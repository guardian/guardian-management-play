package com.gu.management.guice;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.gu.management.status.StatusServlet;
import com.gu.management.status.StatusWriter;
import com.gu.management.switching.Switchable;
import com.gu.management.switching.SwitchboardServlet;

import java.util.Collection;

@Singleton
public class GuiceSwitchboardServlet extends SwitchboardServlet {
    
    @Inject
    public GuiceSwitchboardServlet(Collection<Switchable> switchables) {
        super(switchables);
    }
}