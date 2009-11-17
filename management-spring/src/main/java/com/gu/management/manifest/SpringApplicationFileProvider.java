package com.gu.management.manifest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class SpringApplicationFileProvider implements ApplicationFileProvider, ServletContextAware {

    private static final Logger LOG = Logger.getLogger(ApplicationFileProvider.class);

    private ServletContext servletContext;

    @Required
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public List<String> getFileContents(String relativePath) {
        File file = getFile(relativePath);

        if(!file.exists()) {
            LOG.error("Could not find the application file '" + file.getAbsolutePath() + "'");
            return null;
        }

        try {
            //noinspection unchecked
            return (List<String>) FileUtils.readLines(file);
        } catch( IOException e ) {
            throw new RuntimeException("Error reading application file", e);
        }
    }

    private File getFile(String relativePath) {
        String pathToRootOfApplication;
        if(servletContext != null) {
            pathToRootOfApplication = servletContext.getRealPath("/");
        } else {
             // This is purely for the Spring test because no servlet context is provided
            pathToRootOfApplication = ".";
        }
        return new File(pathToRootOfApplication, relativePath);
    }

    public boolean fileExists(String relativePath) {
        return getFile(relativePath).exists();
    }

    public List<String> getChildDirectories(File parent) {
        FilenameFilter nonDottedDirectories = new AbstractFileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() && !file.getName().startsWith(".");
            }
        };
        String[] filenames = parent.list(nonDottedDirectories);
        return Arrays.asList(filenames);
    }

    public String getAbsolutePath(String relativePath) {
        File file = getFile(relativePath);
        return file.getAbsolutePath();
    }


}
