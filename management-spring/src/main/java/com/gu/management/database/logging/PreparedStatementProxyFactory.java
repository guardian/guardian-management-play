package com.gu.management.database.logging;

import com.gu.management.timing.TimingMetric;

import java.sql.PreparedStatement;

public class PreparedStatementProxyFactory {
    private final TimingMetric metric;

    public PreparedStatementProxyFactory(TimingMetric metric) {
        this.metric = metric;
    }

    PreparedStatement createPreparedStatementProxy(PreparedStatement targetStatement, String sqlQuery) {
        return ProxyHelper.proxy(targetStatement, new PreparedStatementProxy(targetStatement, sqlQuery, metric));
    }
}