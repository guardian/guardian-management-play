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

package com.gu.management.database.checking;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SimpleConnectionChecker implements ConnectionChecker {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleConnectionChecker.class);
	private String query;
	private SessionFactory sessionFactory;

	public SimpleConnectionChecker(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public ConnectionCheckResult check() {
		Session session = null;
		try {
			LOGGER.trace("About to check database connection");
			session = sessionFactory.openSession();
			session.createSQLQuery(query).uniqueResult();
			LOGGER.trace("Database connection check result is successful");
			return new ConnectionCheckResult(true);
		} catch (HibernateException exception) {
			LOGGER.warn("Exception occured during database connection check", exception);
			return new ConnectionCheckResult(exception);
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (HibernateException exception) {
					// Do nothing
				}
			}
		}
	}

}