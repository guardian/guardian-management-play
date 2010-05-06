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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ApplicationFileProvider {
    private static final Logger LOG = Logger.getLogger(ApplicationFileProvider.class);

    private ServletContext servletContext;

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
