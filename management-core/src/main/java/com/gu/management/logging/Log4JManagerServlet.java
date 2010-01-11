package com.gu.management.logging;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

public class Log4JManagerServlet extends HttpServlet {
    private static final String MANAGEMENT_PAGE_HEAD = "<html>\n" +
            "<head><title>Manage log4j levels</title></head>\n" +
            "<body>\n" +
            "\t<form method=\"POST\">\n" +
            "\t\t<input type=\"submit\" value=\"update\" />\n" +
            "\n" +
            "<table>\n" +
            "\t<tr>" +
                "<th>Level</th>" +
                "<th>Effective Level</th>" +
                "<th></th>" +
                "<th>Logger</th>" +
            "</tr>" ;

     private static final String MANAGEMENT_PAGE_FOOT = "</table>\n" +
             "\t\t<input type=\"submit\" value=\"update\" />\n" +
             "\t</form>\n" +
             "</body>\n" +
             "</html>";

    private static final String LOGGER_TABLE_ROW = "<tr>" +
            "<td><select name=\"%1$s\">%2$s</select></td>" +
            "<td>%3$s</td>" +
            "<td><input type=\"submit\" value=\"update\" /></td>" +
            "<td>%1$s</td>" +
            "</tr>";

    private static final Level[] levels = new Level[]{Level.OFF, Level.FATAL, Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG, Level.TRACE, Level.ALL};

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        OutputStreamWriter fileWriter = new OutputStreamWriter(response.getOutputStream());

        fileWriter.write(MANAGEMENT_PAGE_HEAD);

        @SuppressWarnings({"unchecked"})
        List<Logger> loggers = sortLoggers(LogManager.getCurrentLoggers());

        for (Logger logger : loggers) {
            fileWriter.write(String.format(LOGGER_TABLE_ROW, logger.getName(), generateOptionsFor(logger), logger.getEffectiveLevel()));
        }

        fileWriter.write(MANAGEMENT_PAGE_FOOT);

        fileWriter.flush();
    }

    private List<Logger> sortLoggers(Enumeration<Logger> currentLoggers) {
        List<Logger> loggers = new ArrayList<Logger>();
        while(currentLoggers.hasMoreElements()) {
            loggers.add(currentLoggers.nextElement());
        }

        Collections.sort(loggers, new Comparator<Logger>(){
            @Override
            public int compare(Logger logger1, Logger logger2) {
                return logger1.getName().compareTo(logger2.getName());
            }
        });

        return loggers;
    }

    private String generateOptionsFor(Logger logger) {

        StringBuilder builder = new StringBuilder("<option value=\"DEFAULT\" />\n");

        for (Level level : levels) {
            builder.append(String.format("<option value=\"%1$s\" %2$s >%1$s</option>\n", level.toString(), generatedSelectedStringFor(logger, level)));
        }
        return builder.toString();
    }

    private String generatedSelectedStringFor(Logger logger, Level level) {
        return level.equals(logger.getLevel()) ? "selected=\"true\"" : "";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        @SuppressWarnings({"unchecked"})
        Enumeration<Logger> loggers = LogManager.getCurrentLoggers();
        while (loggers.hasMoreElements()) {
            Logger logger = loggers.nextElement();

            String newLogLevel = request.getParameter(logger.getName());
            if (StringUtils.isNotEmpty(newLogLevel)) {
                if ("DEFAULT".equals(newLogLevel)) {
                    logger.setLevel(null);
                } else {
                    logger.setLevel(Level.toLevel(newLogLevel));
                }
            }
        }

        doGet(request, response);
    }
}