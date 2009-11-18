package com.gu.management.database.checking;

import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class SimpleConnectionCheckerTest {

	@Mock SQLQuery sqlQuery;
	@Mock Session session;
	private static final String QUERY = "select 1 from dual";

	@Test
	public void testShouldBeSuccessfulWhenQueryExecutedOnStatementOk() throws Exception {


		ConnectionChecker checker = new ConnectionCheckerBuilder().sqlQuery(sqlQuery).toConnectionChecker();
		when(sqlQuery.uniqueResult()).thenReturn(1);

		ConnectionCheckResult result = checker.check();
		assertThat(result.isSuccessful(), equalTo(true));
	}

	@Test
	public void testShouldHaveCorrectLifecycleForSession() throws Exception {

		ConnectionChecker checker = new ConnectionCheckerBuilder().session(session ).toConnectionChecker();
		checker.check();
	}

	@Test
	public void testShouldFailWhenQueryExecutedOnStatementNotOk() throws Exception {
		Exception exception = new HibernateException(new SQLException("ORA-01089"));

		ConnectionChecker checker = new ConnectionCheckerBuilder().sqlQuery(sqlQuery).toConnectionChecker();
		when(sqlQuery.uniqueResult()).thenThrow(exception);

		ConnectionCheckResult result = checker.check();
		assertThat(result.isSuccessful(), equalTo(false));
		assertThat(result.getFailureCause(), equalTo(exception));
	}


	private class ConnectionCheckerBuilder {
		private @Mock SessionFactory sessionFactory;
		private @Mock Session session;
		private @Mock SQLQuery sqlQuery;


		public ConnectionCheckerBuilder() {
			MockitoAnnotations.initMocks(this);
		}

		public ConnectionCheckerBuilder session(Session session) {
			this.session = session;
			return this;
		}

		public ConnectionCheckerBuilder sqlQuery(SQLQuery sqlQuery) {
			this.sqlQuery = sqlQuery;
			return this;
		}

		public ConnectionChecker toConnectionChecker() {
			when(session.createSQLQuery(QUERY)).thenReturn(sqlQuery);
			when(sessionFactory.openSession()).thenReturn(session);
			SimpleConnectionChecker checker = new SimpleConnectionChecker(sessionFactory);
			checker.setQuery(QUERY);
			return checker;
		}

	}

}