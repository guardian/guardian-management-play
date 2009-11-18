package com.gu.management.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;


public class Log4JManagerServletTest {

    // this is our test subject, we don't log here, that would be silly
    private static final Logger LOGGER = Logger.getLogger(Log4JManagerServletTest.class);

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    private Log4JManagerServlet servlet;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        servlet = new Log4JManagerServlet();
    }

    @Test public void shouldIncludeOurTestLoggerInForm() throws Exception {

        servlet.doGet(request, response);

        assertThat(response.getContentAsString(),
                containsString("<select name=\"com.gu.management.logging.Log4JManagerServletTest\">"));
    }

    @Test public void shouldUpdateLoggingLevelOnSubmit() throws Exception {
        LOGGER.setLevel(Level.DEBUG);

        request.addParameter("com.gu.management.logging.Log4JManagerServletTest", "TRACE");
        servlet.doPost(request, response);

        assertThat(LOGGER.getLevel(), Matchers.equalTo(Level.TRACE));
    }

    @Test public void shouldRedisplayeFormAfterPostRequest() throws Exception {

        servlet.doPost(request, response);

        assertThat(response.getContentAsString(),
                containsString("<select name=\"com.gu.management.logging.Log4JManagerServletTest\">"));
    }

    @Test public void shouldSetLogLevelToNullOnDefaultSubmit() throws Exception {
        LOGGER.setLevel(Level.DEBUG);

        request.addParameter("com.gu.management.logging.Log4JManagerServletTest", "DEFAULT");
        servlet.doPost(request, response);

        assertThat(LOGGER.getLevel(), Matchers.<Object>nullValue());
    }

    @Test public void shouldNotSetLogLevelIfNotSpecifiedinformSubmission() throws Exception {
        LOGGER.setLevel(Level.DEBUG);

        servlet.doPost(request, response);

        assertThat(LOGGER.getLevel(), Matchers.equalTo(Level.DEBUG));
    }
}