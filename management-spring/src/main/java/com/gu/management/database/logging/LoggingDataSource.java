/*
 * Copyright 2010 Guardian News and Media
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gu.management.database.logging;

import com.gu.management.timing.TimingMetric;
import com.mchange.v2.lang.VersionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DelegatingDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class LoggingDataSource extends DelegatingDataSource {

	private static final Logger LOG = LoggerFactory.getLogger(LoggingDataSource.class);

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