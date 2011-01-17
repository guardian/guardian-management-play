package com.gu.management.logging;

import com.gu.management.timing.LoggingStopWatch;
import com.gu.management.timing.NullMetric;
import com.gu.management.timing.TimingMetric;
import com.gu.management.util.VoidCallable;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;

abstract class ConfigurableLoggingFilter extends GuAppServerHeaderFilter {

    protected TimingMetric metric = new NullMetric();

    protected abstract Logger getLogger();

    protected abstract boolean shouldLogParametersOnNonGetRequests();

    protected abstract Set<String> parametersToSuppressInLogs();

    protected abstract Set<String> pathPrefixesToLogAtTrace();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        super.doFilter(servletRequest, servletResponse, filterChain);
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        String logMessage = buildLogMessage(request);
        LoggingStopWatch stopWatch = new RequestLoggingStopWatch(getLogger(), logMessage, getLogLevelFor(request));

        try {
            stopWatch.executeAndLog(new VoidCallable() {
                @Override
                public void voidCall() throws Exception {
                    filterChain.doFilter(request, response);
                }
            });
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            if (shouldFullyLogRequest(request))
                metric.recordTimeSpent(stopWatch.getTime());
        }
    }

    private Level getLogLevelFor(HttpServletRequest request) {
        return shouldFullyLogRequest(request) ? Level.INFO : Level.TRACE;
    }

    public void setMetric(TimingMetric metric) {
        this.metric = metric;
    }

    protected String buildLogMessage(HttpServletRequest request) {
        StringBuilder logMessageBuilder = new StringBuilder(request.getMethod());
        logMessageBuilder.append(" ");
        logMessageBuilder.append(request.getServletPath());

        String pathInfo = request.getPathInfo();
        if (pathInfo == null)
            pathInfo = "";

        logMessageBuilder.append(pathInfo);

        if ("GET".equals(request.getMethod()) || shouldLogParametersOnNonGetRequests()) {
            @SuppressWarnings("unchecked") Enumeration<String> params = request.getParameterNames();

            if (params.hasMoreElements())
                logMessageBuilder.append("?");

            while (params.hasMoreElements()) {
                String paramName = params.nextElement();
                logMessageBuilder.append(paramName);
                logMessageBuilder.append("=");
                logMessageBuilder.append(getOrSuppressParameter(paramName, request));

                if (params.hasMoreElements())
                    logMessageBuilder.append("&");
            }
        }

        return logMessageBuilder.toString();
    }

    private String getOrSuppressParameter(String paramName, HttpServletRequest request) {
        return parametersToSuppressInLogs().contains(paramName) ? "*****" : request.getParameter(paramName);
    }

    private boolean shouldFullyLogRequest(HttpServletRequest request) {
        String fullPath = request.getServletPath() + request.getPathInfo();

        for (String excludedPath : pathPrefixesToLogAtTrace())
            if (fullPath.startsWith(excludedPath))
                return false;

        return true;
    }

    private class RequestLoggingStopWatch extends LoggingStopWatch {
        private RequestLoggingStopWatch(Logger logger, String activity, Level logLevel) {
            super(logger, activity, logLevel);
        }

        @Override
        public void start() {
            log.debug(activity);
            super.start();
        }
    }
}
