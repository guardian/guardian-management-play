package com.gu.management.logging;

import com.gu.management.timing.LoggingStopWatch;
import com.gu.management.timing.NullMetric;
import com.gu.management.timing.TimingMetric;
import com.gu.management.util.ServerIdentityInformation;
import com.gu.management.util.VoidCallable;
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

abstract class ConfigurableLoggingFilter extends AbstractFilter {

    private static final String GU_APP_SERVER_INFO_HEADER = "X-GU-jas";

    protected TimingMetric metric = new NullMetric();
    private ServerIdentityInformation serverIdentityInformation = new ServerIdentityInformation();

    protected abstract Logger getLogger();

    protected abstract boolean shouldLogParametersOnNonGetRequests();

    protected abstract Set<String> parametersToSuppressInLogs();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        String logMessage = buildLogMessage(request);
        LoggingStopWatch stopWatch = getTimerInstance(getLogger(), logMessage);

        addGUAppServerHeader(response);

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
            String fullPath = request.getServletPath() + request.getPathInfo();

            if (!fullPath.startsWith("/management") && !fullPath.startsWith("/status"))
                metric.recordTimeSpent(stopWatch.getTime());
        }
    }

    public void setMetric(TimingMetric metric) {
        this.metric = metric;
    }

    public void setServerIdentityInformation(ServerIdentityInformation serverIdentityInformation) {
        this.serverIdentityInformation = serverIdentityInformation;
    }

    protected LoggingStopWatch getTimerInstance(Logger logger, String logMessage) {
        return new RequestLoggingStopWatch(logger, logMessage);
    }

    protected String getShortVersionOfThreadName() {
        String threadName = Thread.currentThread().getName();
        int lastHyphenPos = threadName.lastIndexOf("-");

        return threadName.substring(lastHyphenPos + 1);
    }

    protected void addGUAppServerHeader(HttpServletResponse response) {
        String threadDigits = getShortVersionOfThreadName();
        String appServerId = serverIdentityInformation.getPublicHostIdentifier();

        StringBuilder headerVal = new StringBuilder();
        headerVal.append(appServerId);
        headerVal.append('-');
        headerVal.append(threadDigits);

        response.addHeader(GU_APP_SERVER_INFO_HEADER, headerVal.toString());
    }

    @SuppressWarnings("unchecked")
    protected String buildLogMessage(HttpServletRequest request) {
        StringBuilder logMessageBuilder = new StringBuilder(request.getMethod());
        logMessageBuilder.append(" ");
        logMessageBuilder.append(request.getServletPath());

        String pathInfo = request.getPathInfo();
        if (pathInfo == null)
            pathInfo = "";

        logMessageBuilder.append(pathInfo);

        if ("GET".equals(request.getMethod()) || shouldLogParametersOnNonGetRequests()) {
            Enumeration<String> params = request.getParameterNames();

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

    protected class RequestLoggingStopWatch extends LoggingStopWatch {
        protected RequestLoggingStopWatch(Logger logger, String activity) {
            super(logger, activity);
        }

        @Override
        public void start() {
            log.debug(activity);
            super.start();
        }
    }
}
