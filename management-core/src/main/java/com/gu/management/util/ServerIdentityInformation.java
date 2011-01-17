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

package com.gu.management.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerIdentityInformation {

    private HostNameProvider hostNameProvider = new HostNameProvider();
    private String hostName;

    public String getPublicHostIdentifier() {
        String returnValue = getHostName();

        if (returnValue.length() > 2)
            return returnValue.substring(returnValue.length() - 2);

        return returnValue;
    }

    private String getHostName() {
        if (hostName == null)
            hostName = hostNameProvider.getHostName();

        return hostName;
    }

    public void setHostNameProvider(HostNameProvider hostNameProvider) {
        this.hostNameProvider = hostNameProvider;
    }

    public String getAsHtmlComment() {
        return String.format("<!--[if !IE]> GUERR (%s) %s <![endif]-->", getPublicHostIdentifier(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS").format(new Date()));
    }
}