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

package com.gu.management.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServerIdentityInformationTest {

	@Mock private HostNameProvider hostNameProvider;
	private ServerIdentityInformation serverIdentityInformation;

	@Before public void setup() {
		serverIdentityInformation = new ServerIdentityInformation();
		serverIdentityInformation.setHostNameProvider(hostNameProvider);
    }

	@Test public void testShouldOnlyGetHostNameOnce() throws Exception {
		when(hostNameProvider.getHostName()).thenReturn("SomeHost");


		serverIdentityInformation.getPublicHostIdentifier();
		serverIdentityInformation.getPublicHostIdentifier();

		verify(hostNameProvider, times(1)).getHostName();
	}

	@Test public void testShouldGetOnlyLastTwoDigitsFromHostname() throws Exception {
		when(hostNameProvider.getHostName()).thenReturn("gurespub07");

		assertEquals("07", serverIdentityInformation.getPublicHostIdentifier());
	}

	@Test public void testShouldFormatServerIdentityInAFormatSuitableForEmbeddingInHtml() throws Exception {
		when(hostNameProvider.getHostName()).thenReturn("gurespub07");

		String htmlComment = serverIdentityInformation.getAsHtmlComment();

		assertThat(htmlComment, startsWith("<!--[if !IE]> GUERR (07) "));
		assertThat(htmlComment, endsWith(" <![endif]-->"));
	}
}