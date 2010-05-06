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
