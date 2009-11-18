package com.gu.management.spring;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletAsControllerAdaptor extends AbstractController {
	private final HttpServlet servlet;

	public ServletAsControllerAdaptor(HttpServlet servlet) {
		this.servlet = servlet;
	}

    @Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		servlet.service(request, response);
		return null;
	}
}