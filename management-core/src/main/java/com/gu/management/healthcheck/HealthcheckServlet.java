package com.gu.management.healthcheck;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HealthcheckServlet extends HttpServlet {

    private HealthcheckEnableSwitch healthCheckEnabledSwitch;

    public HealthcheckServlet(HealthcheckEnableSwitch healthcheckEnableSwitch) {
        this.healthCheckEnabledSwitch = healthcheckEnableSwitch;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (healthCheckEnabledSwitch.isSwitchedOn())
            sendOK(resp);
        else
            sendFailed(resp);
    }

    private void sendOK(HttpServletResponse resp) throws IOException {
        resp.setStatus(200);
        resp.getWriter().println("OK");
    }

    private void sendFailed(HttpServletResponse resp) throws IOException {
        resp.setStatus(503);
        resp.getWriter().println(healthCheckEnabledSwitch.getHealthcheckNotEnabledMessage());
    }
}
