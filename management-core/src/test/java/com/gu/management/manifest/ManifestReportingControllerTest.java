package com.gu.management.manifest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ManifestReportingControllerTest {
	@Mock private Manifest manifest;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		when(manifest.getManifestInformation()).thenReturn("Code Manifest Information");
	}

	@Test
	public void shouldWriteManifestToResponseStream() throws Exception {
		when(manifest.getRevisionNumber()).thenReturn("666");

		MockHttpServletResponse responseMock = new MockHttpServletResponse();

		ManifestReportingServlet servlet = new ManifestReportingServlet(Arrays.asList(manifest));
		servlet.doGet(null, responseMock);

		assertThat(responseMock.getContentType(), equalTo("text/plain"));
		assertThat(responseMock.getContentAsString(), equalTo("Code Manifest Information\n"));

		verify(manifest).reload();
	}

	@Test
	public void shouldNotThrowExceptionWhenCodeOrViewRevisionIsNullJustLikeItWillBeOnDeveloperMachines() throws Exception {
		when(manifest.getRevisionNumber()).thenReturn(null);

		MockHttpServletResponse responseMock = new MockHttpServletResponse();

		ManifestReportingServlet servlet = new ManifestReportingServlet(Arrays.asList(manifest));
		servlet.doGet(null, responseMock);

		assertThat(responseMock.getContentType(), equalTo("text/plain"));
		assertThat(responseMock.getContentAsString(), equalTo("Code Manifest Information\n"));

		verify(manifest).reload();
	}

    @Test
    public void shouldAcceptMultipleManifests() throws Exception {
        Manifest secondManifest = Mockito.mock(Manifest.class);
        when(secondManifest.getRevisionNumber()).thenReturn("42");
        when(manifest.getRevisionNumber()).thenReturn("666");
        when(secondManifest.getManifestInformation()).thenReturn("View Manifest Information");

        MockHttpServletResponse responseMock = new MockHttpServletResponse();

        ManifestReportingServlet servlet = new ManifestReportingServlet(Arrays.asList(manifest, secondManifest));
        servlet.doGet(null, responseMock);

        assertThat(responseMock.getContentType(), equalTo("text/plain"));
        assertThat(responseMock.getContentAsString(), equalTo("Code Manifest Information\nView Manifest Information\n"));

        verify(manifest).reload();

    }
}