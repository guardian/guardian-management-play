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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class SqlQueryData {

	static final int QUERY_PREVIEW_LENGTH = 60;
	private static final Logger LOGGER = Logger.getLogger(SqlQueryData.class);

	private final String rawSqlQuery;
	private final String comment;
	private final String sqlQueryWithoutComment;
	private final Integer id;

	private boolean isExecuted;
	private long timeToRunInMs;

	SqlQueryData(String sqlQuery, Integer id) {
		this.rawSqlQuery = sqlQuery;
		this.isExecuted = false;
		this.comment = extractComment();
		this.sqlQueryWithoutComment = extractQuery();
		this.id = id;
	}

	public SqlQueryData(String sqlQuery) {
		this(sqlQuery, null);
	}

	public String getRawSqlQuery() {
		return rawSqlQuery;
	}

	public String getComment() {
		return comment;
	}

	public String getSqlQuery() {
		return sqlQueryWithoutComment;
	}

	public boolean isExecuted() {
		return isExecuted;
	}

	private String extractComment() {
		String trimmedRawQuery = rawSqlQuery.trim();
		if (trimmedRawQuery.startsWith("/*")) {
			final String comment = extractCommentFromCommentedQuery(trimmedRawQuery);
			if (!"dynamic native SQL query".equals(comment) && !"criteria query".equals(comment)) {
				return comment;
			}
		}

		//noinspection ThrowableInstanceNeverThrown
		LOGGER.trace("No comment on query: " + rawSqlQuery, new DummyExceptionGetAStackTraceDumpedToLog());

		return StringUtils.abbreviate(rawSqlQuery, QUERY_PREVIEW_LENGTH);
	}

	private String extractCommentFromCommentedQuery(String trimmedRawQuery) {
		int commentEndIndex = trimmedRawQuery.indexOf("*/");
		if (commentEndIndex != -1) {
			return trimmedRawQuery.substring(2, commentEndIndex).trim();
		}

		return trimmedRawQuery;
	}

	private String extractQuery() {
		String trimmedRawQuery = rawSqlQuery.trim();
		if (trimmedRawQuery.startsWith("/*")) {
			int commentEndIndex = rawSqlQuery.indexOf("*/");
			if (commentEndIndex != -1) {
				return trimmedRawQuery.substring(commentEndIndex + 2).trim();
			}
		}

		return rawSqlQuery.trim();
	}

	public Integer getId() {
		return this.id;
	}

	public long getTimeToRunInMs() {
		return timeToRunInMs;
	}

	public void setTimeToRunInMs(int timeToRunInMs) {
		this.timeToRunInMs = timeToRunInMs;
	}

	public void notifyIsExecuted(long timeToRunInMs) {
		isExecuted = true;
		this.timeToRunInMs = timeToRunInMs;
	}

	private class DummyExceptionGetAStackTraceDumpedToLog extends Throwable {
	}
}