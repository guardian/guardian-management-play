package com.gu.management.logging;

import com.gu.management.timing.LoggingStopWatch;
import com.gu.management.timing.NullMetric;
import com.gu.management.timing.TimingMetric;
import com.gu.management.util.ServerIdentityInformation;
import com.gu.management.util.VoidCallable;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestLoggingFilter implements Filter {

	private static final String GU_APP_SERVER_INFO_HEADER = "X-GU-jas";
	private static final Logger LOGGER = Logger.getLogger(RequestLoggingFilter.class);

	private TimingMetric metric = new NullMetric();
	private ServerIdentityInformation serverIdentityInformation = new ServerIdentityInformation();

    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
	        throws ServletException, IOException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        LoggingStopWatch stopWatch = new RequestLoggingStopWatch(LOGGER, buildLogMessagePrefix(httpRequest));
		addGUAppServerHeader(httpResponse);

		try {
			stopWatch.executeAndLog(new VoidCallable() {
				@Override public void voidCall() throws Exception {
					filterChain.doFilter(request, response);
				}
			});
		} catch (Exception e) {
			throw new ServletException(e);
		} finally {

			String fullPath = httpRequest.getServletPath() + httpRequest.getPathInfo();
			if (!fullPath.startsWith("/management") && !fullPath.startsWith("/status")) {
				metric.recordTimeSpent(stopWatch.getTime());
			}
		}
	}

	private String buildLogMessagePrefix(HttpServletRequest request) {
		StringBuilder logMessageBuilder = new StringBuilder("Request for ");
		logMessageBuilder.append(request.getServletPath());

		String pathInfo = request.getPathInfo() == null ? "" : request.getPathInfo();

		logMessageBuilder.append(pathInfo);
		String queryString = request.getQueryString();

		if (queryString != null) {
			logMessageBuilder.append("?");
			logMessageBuilder.append(queryString);
		}
		return logMessageBuilder.toString();
	}

	public void setMetric(TimingMetric metric) {
		this.metric = metric;
	}

	void setServerIdentityInformation(ServerIdentityInformation serverIdentityInformation) {
		this.serverIdentityInformation = serverIdentityInformation;
	}

	private String getShortVersionOfThreadName() {
		String threadName = Thread.currentThread().getName();
		int lastHyphenPos = threadName.lastIndexOf("-");

		return threadName.substring(lastHyphenPos + 1);
	}

	private void addGUAppServerHeader(HttpServletResponse response) {
		String threadDigits = getShortVersionOfThreadName();
		String appServerId = serverIdentityInformation.getPublicHostIdentifier();

		StringBuilder headerVal = new StringBuilder();
		headerVal.append(appServerId);
		headerVal.append('-');
		headerVal.append(threadDigits);

		response.addHeader(GU_APP_SERVER_INFO_HEADER, headerVal.toString());
	}

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    private class RequestLoggingStopWatch extends LoggingStopWatch {
		public RequestLoggingStopWatch(Logger logger, String activity) {
			super(logger, activity);
		}

		@Override public void start() {
			log.info(activity);
			super.start();
		}
	}
}