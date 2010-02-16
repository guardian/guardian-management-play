package com.gu.management.timing;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static org.junit.Assert.assertEquals;

public class CountMetricTest {

	private CountMetric metric;

	@Before public void setUp() throws Exception {
		metric = new CountMetric();
	}

	@Test public void testRecordTimeSpentShouldAdjustNumberOfUpdatesAppropriately() throws Exception {
		metric.recordCount(1);
		metric.recordCount(2);
		metric.recordCount(3);

		assertEquals(metric.getCount(), 6);
	}

	@Test public void shouldWriteStatus() throws XMLStreamException {
		metric.setManagementStatusElementName("status element name");
		metric.recordCount(100);
		XMLStreamWriter streamWriter = Mockito.mock(XMLStreamWriter.class);

		metric.writeStatus(streamWriter);

		Mockito.verify(streamWriter).writeStartElement("status element name");
		Mockito.verify(streamWriter).writeCharacters("100");
		Mockito.verify(streamWriter).writeEndElement();
		Mockito.verifyNoMoreInteractions(streamWriter);
	}

}