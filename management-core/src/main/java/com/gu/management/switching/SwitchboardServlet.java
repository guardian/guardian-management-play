package com.gu.management.switching;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

public class SwitchboardServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(SwitchboardServlet.class);

    private final List<Switchable> switches;

    private static final String MANAGEMENT_PAGE_HEAD = "<html>\n" +
            "<head><title>Switchboard</title></head>\n" +
            "<body>\n" +
            "\t<form method=\"POST\">\n" +
            "\t\t<input type=\"submit\" value=\"update\" />\n" +
            "\n" +
            "<table border=\"1\">\n" +
            "\t<tr><th>Switch name</th><th>Description</th><th>State</th><th></th></tr>";

    private static final String MANAGEMENT_PAGE_FOOT = "</table>\n" +
            "\t\t<input type=\"submit\" value=\"update\" />\n" +
            "\t</form>\n" +
            "</body>\n" +
            "</html>";

    private static final String SWITCH_TABLE_ROW = "<tr>" +
            "<td>%1$s</td>" +
            "<td>%2$s</td>" +
            "<td><select name=\"%1$s\">%3$s</select></td>" +
            "<td><input type=\"submit\" value=\"update\" /></td></tr>";

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
        OutputStreamWriter fileWriter = new OutputStreamWriter(response.getOutputStream());

        fileWriter.write(MANAGEMENT_PAGE_HEAD);

        for (Switchable switchable : switches) {
            fileWriter.write(String.format(SWITCH_TABLE_ROW,
                    switchable.getWordForUrl(),
                    switchable.getDescription(),
                    getOptionsFor(switchable)));
        }

        fileWriter.write(MANAGEMENT_PAGE_FOOT);

        fileWriter.flush();
    }

    private String getOptionsFor(Switchable switchable) {
        StringBuilder builder = new StringBuilder();

        builder.append("<option value=\"on\" ")
                .append(switchable.isSwitchedOn() ? "selected" : "")
                .append(">on</option>");

        builder.append("<option value=\"off\" ")
                .append(!switchable.isSwitchedOn() ? "selected" : "")
                .append(">off</option>");

        return builder.toString();
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        for (Switchable switchable : switches) {
            String newState = request.getParameter(switchable.getWordForUrl());

            if ("on".equals(newState)) {
                switchOnWithLogging(switchable);
            } else if ("off".equals(newState)) {
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
