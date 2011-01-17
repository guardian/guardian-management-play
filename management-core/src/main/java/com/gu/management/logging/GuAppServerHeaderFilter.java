package com.gu.management.logging;

import com.gu.management.util.ServerIdentityInformation;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

abstract class GuAppServerHeaderFilter extends AbstractFilter {

    private static final String GU_APP_SERVER_INFO_HEADER = "X-GU-jas";

    private ServerIdentityInformation serverIdentityInformation = new ServerIdentityInformation();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        addGUAppServerHeader((HttpServletResponse) servletResponse);
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

    protected String getShortVersionOfThreadName() {
        String threadName = Thread.currentThread().getName();
        int lastHyphenPos = threadName.lastIndexOf("-");

        return threadName.substring(lastHyphenPos + 1);
    }

    public void setServerIdentityInformation(ServerIdentityInformation serverIdentityInformation) {
        this.serverIdentityInformation = serverIdentityInformation;
    }
}
