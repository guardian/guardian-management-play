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

package com.gu.management.logging;

import com.google.common.collect.ImmutableSet;
import com.gu.management.timing.TimingMetric;
import com.gu.management.util.ServerIdentityInformation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RequestLoggingFilterTest {

    private static final String GU_APP_SERVER_INFO_HEADER = "X-GU-jas";

    @Mock
    private TimingMetric metric;
    @Mock
    private FilterChain filterChain;
    @Mock
    private ServerIdentityInformation serverIdentityInformation;

    private MockHttpServletResponse response;
    private MockHttpServletRequest request;
    private RequestLoggingFilter filter;

    @Before
    public void setUp() throws Exception {
        filter = new RequestLoggingFilter();
        configureFilter(filter);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        request.setMethod("GET");
    }

    @Test
    public void testLogsGetPrefix() throws Exception {
        request.setPathInfo("/foo/bar");
        assertThat(filter.buildLogMessage(request), is("GET /foo/bar"));
    }

    @Test
    public void testLogsGetParameters() throws Exception {
        request.setPathInfo("/foo/bar");
        request.setParameter("foo", "foo");
        request.setParameter("bar", "bar");
        request.setQueryString("foo=foo&bar=bar");

        assertThat(filter.buildLogMessage(request), is("GET /foo/bar?foo=foo&bar=bar"));
    }

    @Test
    public void testDefaultFilterDoesNotLogPostParameters() throws Exception {
        request.setPathInfo("/foo/bar");
        request.setParameter("foo", "foo");
        request.setMethod("POST");

        assertThat(filter.buildLogMessage(request), is("POST /foo/bar"));
    }

    @Test
    public void testCanConfigureMaxSizeForPostParameters() throws Exception {
        request.setPathInfo("/foo/bar");
        request.setParameter("foo", "abcdefghijklmnopqrstuvwxyz");
        request.setMethod("POST");

        filter = configureFilter(new RequestLoggingFilter() {
            @Override
            protected int maximumSizeForPostParameters() {
                return 10;
            }

            @Override
            protected boolean shouldLogParametersOnNonGetRequests() {
                return true;
            }
        });

        assertThat(filter.buildLogMessage(request), is("POST /foo/bar?foo=abcdefghij..."));
    }

    @Test
    public void testFilterCanBeConfiguredToLogPostParameters() throws Exception {
        filter = configureFilter(new RequestLoggingFilter() {
            @Override
            protected boolean shouldLogParametersOnNonGetRequests() {
                return true;
            }
        });

        request.setMethod("POST");
        request.setParameter("foo", "foo");
        request.setPathInfo("/foo/bar");

        assertThat(filter.buildLogMessage(request), is("POST /foo/bar?foo=foo"));
    }

    @Test
    public void testCanConfigureFilterToSuppressCertainParameters() throws Exception {
        filter = configureFilter(new RequestLoggingFilter() {
            @Override
            protected boolean shouldLogParametersOnNonGetRequests() {
                return true;
            }

            @Override
            protected Set<String> parametersToSuppressInLogs() {
                return ImmutableSet.of("secret", "password");
            }
        });

        request.setMethod("POST");
        request.setPathInfo("/foo/bar");
        request.setParameter("foo", "foo");
        request.setParameter("password", "password");
        request.setParameter("secret", "secret");

        assertThat(filter.buildLogMessage(request), is("POST /foo/bar?foo=foo&password=*****&secret=*****"));
    }

    @Test
    public void testLogsPostPrefix() throws Exception {
        request.setPathInfo("/foo/bar");
        request.setMethod("POST");

        assertThat(filter.buildLogMessage(request), is("POST /foo/bar"));
    }

    @Test
    public void testShouldIncrementMetricForSuccessfulRequest() throws Exception {
        filter.doFilter(request, response, filterChain);
        verify(metric).recordTimeSpent(anyInt());
    }

    @Test
    public void testShouldIncrementMetricForUnSuccessfulRequest() throws Exception {
        doThrow(new RuntimeException()).when(filterChain).doFilter((HttpServletRequest) anyObject(), (HttpServletResponse) anyObject());

        try {
            filter.doFilter(request, response, filterChain);
        } catch (ServletException ex) {
            // in real life, this propagates up
        }

        verify(metric).recordTimeSpent(anyInt());
    }

    @Test
    public void testShouldNotInvokeMetricForManagementUrls() throws Exception {
        request.setServletPath("/management");
        filter.doFilter(request, response, filterChain);
        verifyZeroInteractions(metric);
    }

    @Test
    public void testShouldNotInvokeMetricForGDNStatusPage() throws Exception {
        request.setServletPath("/status");
        filter.doFilter(request, response, filterChain);
        verifyZeroInteractions(metric);
    }

    @Test
    public void testShouldPutGUAppServerHeaderOnResponse() throws Exception {
        when(serverIdentityInformation.getPublicHostIdentifier()).thenReturn("03");
        Thread.currentThread().setName("resin-tcp-connection-respub.gul3.gnl:6802-1234");
        String expectedAppServerHeader = "03-1234";

        filter.doFilter(request, response, filterChain);

        assertEquals("AppServer head doesn't match", expectedAppServerHeader, response.getHeader(GU_APP_SERVER_INFO_HEADER).toString());
    }

    @Test
    public void testShouldPutGuAppServerHeaderOnResponseEvenIfFilterChainThrowsException() throws Exception {
        when(serverIdentityInformation.getPublicHostIdentifier()).thenReturn("07");
        doThrow(new ServletException()).when(filterChain).doFilter(request, response);
        Thread.currentThread().setName("resin-tcp-connection-*:8080-35");
        String expectedAppServerHeader = "07-35";

        try {
            filter.doFilter(request, response, filterChain);
        } catch (ServletException ex) {
            // in real life, this propagates up
        }

        assertEquals("AppServer head doesn't match", expectedAppServerHeader, response.getHeader(GU_APP_SERVER_INFO_HEADER).toString());
    }

    private RequestLoggingFilter configureFilter(RequestLoggingFilter theFilter) {
        theFilter.setMetric(metric);
        theFilter.setServerIdentityInformation(serverIdentityInformation);
        return theFilter;
    }
}