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
