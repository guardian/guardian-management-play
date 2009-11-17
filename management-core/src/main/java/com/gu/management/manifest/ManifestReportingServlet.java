package com.gu.management.manifest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


public class ManifestReportingServlet extends HttpServlet {

    private final List<Manifest> manifestList;

    public ManifestReportingServlet(List<Manifest> manifestList) {
		this.manifestList = manifestList;
	}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/plain");

        PrintWriter writer = response.getWriter();
        for (Manifest manifest : manifestList) {
            manifest.reload();

            writer.write(manifest.getManifestInformation() + "\n");
        }

		writer.flush();
	}

}