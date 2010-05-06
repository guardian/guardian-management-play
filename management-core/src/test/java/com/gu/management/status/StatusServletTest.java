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

package com.gu.management.status;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.xml.stream.XMLStreamWriter;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class StatusServletTest {

	private StatusServlet statusController;
	private MockHttpServletResponse servletResponse;
	private List<StatusWriter> statusWriters;

	@Before
	public void setUp() {
		statusWriters = asList(
                mock(StatusWriter.class),
                mock(StatusWriter.class),
                mock(StatusWriter.class));
		statusController = new StatusServlet(statusWriters);
		servletResponse = new MockHttpServletResponse();
	}

	@Test
	public void shouldSetContentTypeToTextXml() throws Exception {
		statusController.doGet(null, servletResponse);
		assertThat(servletResponse.getContentType(), equalTo("text/xml"));
	}

	@Test
	public void shouldCallAllSuppliedStatusWriters() throws Exception {
		statusController.doGet(null, servletResponse);

		for (StatusWriter statusWriter : statusWriters) {
			verify(statusWriter).writeStatus((XMLStreamWriter) Mockito.anyObject());
		}
	}

}