package com.gu.management.guice;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletUrlPatternsDiscoveryService;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Singleton
public class ManagementUrlDiscoveryServlet extends HttpServlet {

    private ServletContext servletContext;
    private ServletUrlPatternsDiscoveryService urlPatternDiscoveryService;

    @Inject
    public ManagementUrlDiscoveryServlet(ServletContext servletContext, ServletUrlPatternsDiscoveryService urlPatternDiscoveryService) {
        this.servletContext = servletContext;
        this.urlPatternDiscoveryService = urlPatternDiscoveryService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter writer = response.getWriter();
        writer.write("<html><head><title>Management URLs</title></head><body><h2>Management URLs</h2><ul>\n");


        String contextPath = servletContext.getContextPath();

        List<String> urlPatterns = urlPatternDiscoveryService.getServletsUrlPatterns();
        for (String urlPattern : urlPatterns) {
            if (urlPattern.startsWith("/management/")) {
                String path = contextPath + urlPattern;
                writer.printf("<li><a href=\"%s\">%s</a></li>\n", path, urlPattern.replace("/management", ""));
            }
        }

        writer.write("</ul></body></html>");
    }

}