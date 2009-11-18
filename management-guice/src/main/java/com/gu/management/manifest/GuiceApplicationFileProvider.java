package com.gu.management.manifest;

import com.google.inject.Inject;

import javax.servlet.ServletContext;

public class GuiceApplicationFileProvider extends ApplicationFileProvider{

    @Inject
    public GuiceApplicationFileProvider(ServletContext servletContext) {
        setServletContext(servletContext);
    }

}
