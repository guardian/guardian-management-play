package com.gu.management.healthcheck;

import com.gu.management.switching.SwitchableState;

public class HealthcheckEnableSwitch extends SwitchableState {

    private static final String HEALTHCARE_OFF = "Service unavailable: healthcheck-enable switch is OFF";

    public String getHealthcheckNotEnabledMessage() {
        return HEALTHCARE_OFF;
    }

    @Override
    public String getDescription() {
        return "Health check enable";
    }

    @Override
    public String getWordForUrl() {
        return "healthcheck-enable";
    }
}
