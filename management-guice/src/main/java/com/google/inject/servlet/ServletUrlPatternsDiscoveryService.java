package com.google.inject.servlet;

import com.google.inject.*;

import java.util.ArrayList;
import java.util.List;

public class ServletUrlPatternsDiscoveryService {

    private static final TypeLiteral<List<ServletDefinition>> SERVLET_DEFS =
            new TypeLiteral<List<ServletDefinition>>() {
            };
    private Injector injector;

    @Inject
    public ServletUrlPatternsDiscoveryService(Injector injector) {
        this.injector = injector;
    }

    public List<String> getServletsUrlPatterns() {

        List<String> urlPatterns = new ArrayList<String>();

        for (Binding<?> binding : injector.findBindingsByType(SERVLET_DEFS)) {

            @SuppressWarnings("unchecked") //guarded by findBindingsByType()
            Key<List<ServletDefinition>> defsKey = (Key<List<ServletDefinition>>) binding.getKey();
            List<ServletDefinition> servletDefinitions = injector.getInstance(defsKey);

            for (ServletDefinition definition : servletDefinitions) {
                urlPatterns.add(definition.getPattern());
            }
        }

        return urlPatterns;
    }


}
