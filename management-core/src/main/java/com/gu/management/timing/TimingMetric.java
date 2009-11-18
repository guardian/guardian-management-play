package com.gu.management.timing;

import com.gu.management.status.StatusWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.concurrent.atomic.AtomicLong;

public class TimingMetric extends StatusWriter {

	private AtomicLong totalTimeInMillis = new AtomicLong();
	private AtomicLong count = new AtomicLong();
	private String managementStatusElementName;

	public void recordTimeSpent(long durationInMillis) {
		totalTimeInMillis.addAndGet(durationInMillis);
		count.incrementAndGet();
	}

	public long getTotalTimeInMillis() {
		return totalTimeInMillis.get();
	}

	public long getCount() {
		return count.get();
	}

	public String getManagementStatusElementName() {
		return managementStatusElementName;
	}

	public void setManagementStatusElementName(String name) {
		this.managementStatusElementName = name;
	}

	@Override public void writeStatus(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
		xmlStreamWriter.writeStartElement(managementStatusElementName);
		xmlStreamWriter.writeStartElement("count");
		xmlStreamWriter.writeCharacters(count.toString());
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeStartElement("totalTimeInMillis");
		xmlStreamWriter.writeCharacters(totalTimeInMillis.toString());
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();
    }
}