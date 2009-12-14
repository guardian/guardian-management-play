package com.gu.management.spring;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;


public class ManagementUrlDiscoveryController extends AbstractController {

	ManagementUrlDiscoveryService service;

	public ManagementUrlDiscoveryController(ManagementUrlDiscoveryService service) {
		this.service = service;
	}

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("text/html;charset=UTF-8");

		PrintWriter writer = response.getWriter();

		writer.write("<html><head><title>Management URLs</title></head><body><h2>Management URLs</h2><ul>\n");
		Collection<String> managementUrls = service.getManagementUrls();
		for (String url : managementUrls) {
			if (!"/management".equals(url)) {
 	            writer.printf("<li><a href=\"management%s\">%s</a></li>\n", url, url);
 	        }
		}

		writer.write("</ul></body></html>");

		return null;
	}
}
