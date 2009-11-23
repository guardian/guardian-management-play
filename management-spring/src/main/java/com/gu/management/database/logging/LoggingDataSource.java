package com.gu.management.database.logging;

import com.gu.management.timing.TimingMetric;
import com.mchange.v2.lang.VersionUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DelegatingDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class LoggingDataSource extends DelegatingDataSource {

	private static final Logger LOG = Logger.getLogger(LoggingDataSource.class);

    private final PreparedStatementProxyFactory preparedStatementProxyFactory;


    public LoggingDataSource(TimingMetric metric) {
        this.preparedStatementProxyFactory = new PreparedStatementProxyFactory(metric);

        if (VersionUtils.isAtLeastJavaVersion14()) {
			LOG.info("Successfully initiated the class "+VersionUtils.class+" as a workaround for bug #13545");
		}
	}

	@Override
	public Connection getConnection() throws SQLException {
		Connection targetConnection = super.getConnection();
		return ProxyHelper.proxy(targetConnection,
				new ConnectionProxy(targetConnection, preparedStatementProxyFactory));
	}
}