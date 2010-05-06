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

package com.gu.management.manifest;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;

import javax.servlet.ServletContext;

import static org.mockito.Mockito.mock;

public class ManifestModuleTest {
    @Test
    public void canInstantiateManifestServlet() {
        Injector injector = Guice.createInjector(new DummyServletModule(), new ManifestModule());
        injector.getInstance(ManifestReportingServlet.class);
    }

    private class DummyServletModule extends AbstractModule {
        @Override
        protected void configure() {
            // this exists because the guice servlet extensions automaticallly include
            // servlet context, so this just emulates that
            bind(ServletContext.class).toInstance(mock(ServletContext.class));
        }
    }
}
