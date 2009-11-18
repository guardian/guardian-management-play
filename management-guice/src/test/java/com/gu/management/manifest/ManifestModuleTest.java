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
