package com.gu.management.manifest;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.util.Arrays;

public class ManifestModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ApplicationFileProvider.class).to(GuiceApplicationFileProvider.class);
    }

    @Provides @Singleton
    public ManifestReportingServlet servlet(Manifest manifest) {
        return new ManifestReportingServlet(Arrays.asList(manifest));
    }

    @Provides
    public Manifest manifest(ApplicationFileProvider applicationFileProvider) {
        return new Manifest(applicationFileProvider);
    }
}
