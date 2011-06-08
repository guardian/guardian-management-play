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

package com.gu.management.switching;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

public class SwitchboardServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(SwitchboardServlet.class);

    private final List<Switchable> switches;

    private static final String MANAGEMENT_PAGE_HEAD = "<html>\n" +
            "<head><title>Switchboard</title></head>\n" +
            "<body>\n" +
            "\t<form method=\"POST\">\n" +
            "<table border=\"1\">\n" +
            "\t<tr><th>Switch name</th><th>Description</th><th>State</th></tr>";

    private static final String MANAGEMENT_PAGE_FOOT =
            "</table>\n" +
            "\t</form>\n" +
            "</body>\n" +
            "</html>";

    private static final String SWITCH_TABLE_ROW = "<tr>" +
            "<td><a href=\"?switch=%1$s\">%1$s</a></td>" +
            "<td>%2$s</td>" +
            "<td style=\"width: 100px; text-align: center;\">%3$s</td>" +
            "</tr>";

    public SwitchboardServlet(Collection<Switchable> switches) {
        this.switches = new ArrayList<Switchable>(switches);
        Collections.sort(this.switches, new Comparator<Switchable>() {
            @Override
            public int compare(Switchable o1, Switchable o2) {
                return o1.getWordForUrl().compareTo(o2.getWordForUrl());
            }
        });
   }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");

        String switchToShow = request.getParameter("switch");

        OutputStreamWriter fileWriter = new OutputStreamWriter(response.getOutputStream());

        fileWriter.write(MANAGEMENT_PAGE_HEAD);

        for (Switchable switchable : switches) {

            if (StringUtils.isEmpty(switchToShow) || switchToShow.equals(switchable.getWordForUrl())) {
                fileWriter.write(String.format(SWITCH_TABLE_ROW,
                        switchable.getWordForUrl(),
                        switchable.getDescription(),
                        getButtonsFor(switchable)));
            }
        }

        fileWriter.write(MANAGEMENT_PAGE_FOOT);

        fileWriter.flush();
    }

    private String getButtonsFor(Switchable switchable) {
        if (switchable.isSwitchedOn()) {
            return String.format(
                    "<span style=\"color: ForestGreen\"> ON </span>" +
                    "<input type=\"submit\" name=\"%s\" value=\"OFF\" />",
                    switchable.getWordForUrl());
        } else {
            return String.format(
                    "<input type=\"submit\" name=\"%s\" value=\"ON\"/>" +
                    "<span style=\"color: DarkRed\"> OFF </span>", switchable.getWordForUrl());
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        for (Switchable switchable : switches) {
            String newState = request.getParameter(switchable.getWordForUrl());

            if ("ON".equalsIgnoreCase(newState)) {
                switchOnWithLogging(switchable);
            } else if ("OFF".equalsIgnoreCase(newState)) {
                switchOffWithLogging(switchable);
            }
        }

        doGet(request, response);
    }

    private void switchOffWithLogging(Switchable switchable) {
        if (switchable.isSwitchedOn()) {
            LOG.info("Switching off " + switchable.getWordForUrl());
            switchable.switchOff();
        }
    }

    private void switchOnWithLogging(Switchable switchable) {
        if (!switchable.isSwitchedOn()) {
            LOG.info("Switching on " + switchable.getWordForUrl());
            switchable.switchOn();
        }
    }


}
