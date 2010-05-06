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

package com.gu.management.timing;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static org.junit.Assert.assertEquals;

public class TimingMetricTest {

	private TimingMetric metric;

	@Before public void setUp() throws Exception {
		metric = new TimingMetric();
	}

	@Test public void testRecordTimeSpentShouldAdjustTotalAppropriately() throws Exception {
		metric.recordTimeSpent(37);
		metric.recordTimeSpent(27);

		assertEquals(metric.getTotalTimeInMillis(), 37 + 27);
	}

	@Test public void testRecordTimeSpentShouldAdjustNumberOfUpdatesAppropriately() throws Exception {
		metric.recordTimeSpent(1);
		metric.recordTimeSpent(1);
		metric.recordTimeSpent(1);

		assertEquals(metric.getCount(), 3);
	}

	@Test public void shouldWriteStatus() throws XMLStreamException {
		metric.setManagementStatusElementName("status element name");
		metric.recordTimeSpent(100);
		metric.recordTimeSpent(100);
		metric.recordTimeSpent(100);
		metric.recordTimeSpent(100);
		XMLStreamWriter streamWriter = Mockito.mock(XMLStreamWriter.class);

		metric.writeStatus(streamWriter);

		Mockito.verify(streamWriter).writeStartElement("status element name");
		Mockito.verify(streamWriter).writeStartElement("count");
		Mockito.verify(streamWriter).writeCharacters("4");
		Mockito.verify(streamWriter).writeStartElement("totalTimeInMillis");
		Mockito.verify(streamWriter).writeCharacters("400");
		Mockito.verify(streamWriter, Mockito.times(3)).writeEndElement();
		Mockito.verifyNoMoreInteractions(streamWriter);
	}

}