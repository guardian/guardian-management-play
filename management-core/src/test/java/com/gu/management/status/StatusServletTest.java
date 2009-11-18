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