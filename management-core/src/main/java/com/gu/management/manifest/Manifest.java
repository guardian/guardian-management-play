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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manifest {

	private static final Logger LOGGER = LoggerFactory.getLogger(Manifest.class);

	private Long revisionNumber;
	private String absolutePath;
	private String manifestString;
	private String manifestFilePath = "META-INF/MANIFEST.MF";
	private ApplicationFileProvider fileProvider;
    private Map<String, String> values = new HashMap<String, String>();

    public Manifest(ApplicationFileProvider fileProvider) {
        this.fileProvider=fileProvider;
    }

    public void setManifestFilePath(String manifestFilePath) {
        this.manifestFilePath = manifestFilePath;
        reload();
    }

    public Long getRevisionNumber() {
        return revisionNumber;
    }


    public String getManifestInformation() {
        if(absolutePath != null) {
            return "Absolute-Path: " + absolutePath + "\n" + manifestString;
        }
        return manifestString;
    }

    private String getValue(String line) {
		String[] splits = line.split(":");
		return splits[1].trim();
	}

    private void parseRevisionNumber(String line) {
		try {
			revisionNumber = Long.parseLong(getValue(line));
		} catch(NumberFormatException e) {
			LOGGER.info("Could not parse revision number from '" + line + "'; using 0 instead");
            revisionNumber = 0L;
		}
	}

    @Override
	public String toString() {
		return "Manifest: " + getManifestInformation();
	}

    public void reload() {
		LOGGER.info("Reloading manifest: "+manifestFilePath);
        values = new HashMap<String, String>();
		List<String> file = fileProvider.getFileContents(manifestFilePath);
		if(file != null) {
			parseManifest(file);
		} else {
			manifestString = String.format("Manifest file not found: '%s", fileProvider.getAbsolutePath(manifestFilePath)) + "'";
			revisionNumber = System.currentTimeMillis();
			LOGGER.debug("Manifest not found generating random revision number : "+revisionNumber);
		}
	}

	private void parseManifest(List<String> file) {
		absolutePath = fileProvider.getAbsolutePath(manifestFilePath);
		manifestString = "";

		for (String line : file) {
			manifestString += line + "\n";
			if(line.startsWith("Revision") ) {
				parseRevisionNumber(line);
				LOGGER.info("Manifest Revision: "+revisionNumber);
			}
            addKeyValue(line);
		}
	}

    private void addKeyValue(String line){
        String[] spliters = line.split(":");
        if (spliters.length == 2){
            String key = spliters[0].trim();
            String value = spliters[1].trim();
            values.put(key, value);
        }
    }

    public String getValueFor(String key) {
        return values.get(key);
    }

    public String getValueFor(String key, String defaultValue){
        String value = getValueFor(key);
        return value != null ? value : defaultValue; 
    }
}