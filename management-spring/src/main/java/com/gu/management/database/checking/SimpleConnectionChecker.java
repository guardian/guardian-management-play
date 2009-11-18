package com.gu.management.database.checking;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;


public class SimpleConnectionChecker implements ConnectionChecker {

	private static final Logger LOGGER = Logger.getLogger(SimpleConnectionChecker.class);
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