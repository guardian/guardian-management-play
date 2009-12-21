package com.gu.management.guice;

import com.google.inject.internal.Lists;
import com.google.inject.servlet.ServletUrlPatternsDiscoveryService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;


public class ManagementUrlDiscoveryServletTest {

    @Mock
    ServletContext context;
    @Mock
    ServletUrlPatternsDiscoveryService urlPatternDiscoveryService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldOnlyListManagementUrls() throws IOException, ServletException {
        when(context.getContextPath()).thenReturn("/mywebapp");
        when(urlPatternDiscoveryService.getServletsUrlPatterns())
               .thenReturn(Lists.<String>newArrayList("/hello", "/management/manifest"));

        ManagementUrlDiscoveryServlet discoveryServlet = new ManagementUrlDiscoveryServlet(context, urlPatternDiscoveryService);
        MockHttpServletResponse response = new MockHttpServletResponse();

        discoveryServlet.doGet(new MockHttpServletRequest(), response);
        assertThat(response.getContentAsString(), containsString("<li><a href=\"/mywebapp/management/manifest\">/manifest</a></li>"));
        assertThat(response.getContentAsString(), Matchers.not(containsString("/hello")));
    }
}
