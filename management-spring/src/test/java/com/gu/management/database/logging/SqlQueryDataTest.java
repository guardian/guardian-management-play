package com.gu.management.database.logging;

import junit.framework.TestCase;
import org.apache.commons.lang.StringUtils;


public class SqlQueryDataTest extends TestCase {

	private String sqlQueryWithComment;
	private String sqlQueryWithoutComment;
	private String sqlQueryWithInlineComment;
	private String sqlQueryWithUnterminatedComment;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sqlQueryWithComment = "/* dual */ select * from dual";
		sqlQueryWithUnterminatedComment = "/* dual frefefew few fewf";
		sqlQueryWithInlineComment = "select * from /* foo */ dual";
		sqlQueryWithoutComment = "select * from dual inner join some other table on some parameter";
	}

	public void testCanNotifyOfQueryExecution() throws Exception {
		SqlQueryData sqlQueryData = new SqlQueryData("select *", null);
		assertFalse(sqlQueryData.isExecuted());
		sqlQueryData.notifyIsExecuted(10);
		assertTrue(sqlQueryData.isExecuted());
		assertEquals(10, sqlQueryData.getTimeToRunInMs());
	}

	public void testCanHandleShortQueries() throws Exception {
		SqlQueryData sqlQueryData = new SqlQueryData("select *", null);

		assertEquals("select *", sqlQueryData.getRawSqlQuery());
		assertEquals("select *", sqlQueryData.getComment());
		assertEquals("select *", sqlQueryData.getSqlQuery());
    }

	public void testCanHandleQueriesWithComments() throws Exception {
		SqlQueryData sqlQueryData = new SqlQueryData(sqlQueryWithComment, null);

		assertEquals(sqlQueryWithComment, sqlQueryData.getRawSqlQuery());
		assertEquals("dual", sqlQueryData.getComment());
		assertEquals("select * from dual", sqlQueryData.getSqlQuery());
	}

	public void testCanHandleQueryWithInlineComment() throws Exception {
		SqlQueryData sqlQueryData = new SqlQueryData(sqlQueryWithInlineComment, null);

		assertEquals(sqlQueryWithInlineComment, sqlQueryData.getRawSqlQuery());
		assertEquals(stringPreview(sqlQueryWithInlineComment), sqlQueryData.getComment());
		assertEquals(sqlQueryWithInlineComment, sqlQueryData.getSqlQuery());
	}

	private String stringPreview(String sql) {
		return StringUtils.abbreviate(sql, 60);
	}

	public void testCanHandleQueriesWithoutComment() throws Exception {
		SqlQueryData sqlQueryData = new SqlQueryData(sqlQueryWithoutComment, null);

		assertEquals(sqlQueryWithoutComment, sqlQueryData.getRawSqlQuery());
		assertEquals(stringPreview(sqlQueryWithoutComment), sqlQueryData.getComment());
		assertEquals(sqlQueryWithoutComment, sqlQueryData.getSqlQuery());
	}

	public void testCanHandleQueriesWithUnterminatedComments() throws Exception {
		SqlQueryData sqlQueryData = new SqlQueryData(sqlQueryWithUnterminatedComment, null);

		assertEquals(sqlQueryWithUnterminatedComment, sqlQueryData.getRawSqlQuery());
		assertEquals(sqlQueryWithUnterminatedComment, sqlQueryData.getComment());
		assertEquals(sqlQueryWithUnterminatedComment, sqlQueryData.getSqlQuery());
	}

	public void testShouldIgnoreCommentIfItIsDynamicNativeSqlQuery() throws Exception {
		final String query = "/* dynamic native SQL query */ opps stupid developer";
		SqlQueryData sqlQueryData = new SqlQueryData(query, null);

		assertEquals(query, sqlQueryData.getRawSqlQuery());
		assertEquals(stringPreview(query), sqlQueryData.getComment());
		assertEquals("opps stupid developer", sqlQueryData.getSqlQuery());
	}

	public void testShouldIgnoreCommentIfItIsCriteriaquery() throws Exception {
		final String query = "/* criteria query */ opps stupid developer";
		SqlQueryData sqlQueryData = new SqlQueryData(query, null);

		assertEquals(query, sqlQueryData.getRawSqlQuery());
		assertEquals(stringPreview(query), sqlQueryData.getComment());
		assertEquals("opps stupid developer", sqlQueryData.getSqlQuery());

	}
}