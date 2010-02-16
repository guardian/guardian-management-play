package com.gu.management.timing;

import com.gu.management.status.StatusWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.concurrent.atomic.AtomicLong;

public class CountMetric extends StatusWriter {

	private AtomicLong count = new AtomicLong();
	private String managementStatusElementName;

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
		xmlStreamWriter.writeCharacters(count.toString());
		xmlStreamWriter.writeEndElement();
    }

    public void recordCount(int count) {
        this.count.addAndGet(count);
    }
}