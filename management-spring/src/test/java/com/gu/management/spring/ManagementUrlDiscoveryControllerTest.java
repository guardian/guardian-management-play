package com.gu.management.spring;

import static java.util.Arrays.asList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletResponse;


public class ManagementUrlDiscoveryControllerTest {

	@Mock
	private ManagementUrlDiscoveryService service;

	@Before
	public void init() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldShowSuppliedUrls() throws Exception {
		ManagementUrlDiscoveryController controller = new ManagementUrlDiscoveryController(service);

		MockHttpServletResponse response = new MockHttpServletResponse();

		List<String> urls = asList("/url1");
		when(service.getManagementUrls()).thenReturn(urls);

		controller.handleRequestInternal(null, response);

		assertThat(response.getContentAsString(), containsString("<a href=\"management/url1\">/url1</a>"));
	}
}
