package com.gu.management.logging;

import com.gu.management.timing.TimingMetric;
import com.gu.management.util.ServerIdentityInformation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RequestLoggingFilterTest {

	private static final String GU_APP_SERVER_INFO_HEADER = "X-GU-jas";

	@Mock private TimingMetric metric;
	@Mock private HttpServletRequest request;
	@Mock private FilterChain filterChain;
	@Mock private ServerIdentityInformation serverIdentityInformation;
	private MockHttpServletResponse response;

	private RequestLoggingFilter filter;

	@Before public void setUp() throws Exception {
		filter = new RequestLoggingFilter();
		filter.setMetric(metric);
		filter.setServerIdentityInformation(serverIdentityInformation);
		response = new MockHttpServletResponse();
		when(request.getServletPath()).thenReturn("");
	}

	@Test public void testShouldIncrementMetricForSuccessfulRequest() throws Exception {
		filter.doFilter(request, response, filterChain);
		verify(metric).recordTimeSpent(anyInt());
	}

	@Test public void testShouldIncrementMetricForUnSuccessfulRequest() throws Exception {
		doThrow(new RuntimeException()).when(filterChain).doFilter((HttpServletRequest) anyObject(), (HttpServletResponse) anyObject());

		try {
			filter.doFilter(request, response, filterChain);
		} catch (ServletException ex) { // in real life, this propagates up
		}

		verify(metric).recordTimeSpent(anyInt());
	}

	@Test public void testShouldNotInvokeMetricForManagementUrls() throws Exception {
		when(request.getServletPath()).thenReturn("/management");
		filter.doFilter(request, response, filterChain);
		verifyZeroInteractions(metric);
	}

	@Test public void testShouldNotInvokeMetricForGDNStatusPage() throws Exception {
		when(request.getServletPath()).thenReturn("/status");
		filter.doFilter(request, response, filterChain);
		verifyZeroInteractions(metric);
	}


	@Test public void testShouldPutGUAppServerHeaderOnResponse() throws Exception {
		when(serverIdentityInformation.getPublicHostIdentifier()).thenReturn("03");
		Thread.currentThread().setName("resin-tcp-connection-respub.gul3.gnl:6802-1234");
		String expectedAppServerHeader = "03-1234";

		filter.doFilter(request, response, filterChain);

		assertEquals("AppServer head doesn't match", expectedAppServerHeader, response.getHeader(GU_APP_SERVER_INFO_HEADER).toString());
	}

	@Test public void testShouldPutGuAppServerHeaderOnResponseEvenIfFilterChainThrowsException() throws Exception {
		when(serverIdentityInformation.getPublicHostIdentifier()).thenReturn("07");
		doThrow(new ServletException()).when(filterChain).doFilter(request, response);
		Thread.currentThread().setName("resin-tcp-connection-*:8080-35");
		String expectedAppServerHeader = "07-35";

		try {
			filter.doFilter(request, response, filterChain);
		} catch (ServletException ex) { // in real life, this propagates up
		}

		assertEquals("AppServer head doesn't match", expectedAppServerHeader, response.getHeader(GU_APP_SERVER_INFO_HEADER).toString());

	}
}