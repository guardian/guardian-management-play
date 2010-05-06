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
		when(manifest.getRevisionNumber()).thenReturn(666L);

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
        when(secondManifest.getRevisionNumber()).thenReturn(42L);
        when(manifest.getRevisionNumber()).thenReturn(666L);
        when(secondManifest.getManifestInformation()).thenReturn("View Manifest Information");

        MockHttpServletResponse responseMock = new MockHttpServletResponse();

        ManifestReportingServlet servlet = new ManifestReportingServlet(Arrays.asList(manifest, secondManifest));
        servlet.doGet(null, responseMock);

        assertThat(responseMock.getContentType(), equalTo("text/plain"));
        assertThat(responseMock.getContentAsString(), equalTo("Code Manifest Information\nView Manifest Information\n"));

        verify(manifest).reload();

    }
}