package com.gu.management.timing;

import com.gu.management.status.StatusWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class TimestampMetric extends StatusWriter {
	private Date timeStamp = null;
	private String managementStatusElementName;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

    public Date getTimeStamp() {
		return timeStamp;
	}

	public String getManagementStatusElementName() {
		return managementStatusElementName;
	}

	public void setManagementStatusElementName(String name) {
		this.managementStatusElementName = name;
	}

	@Override public void writeStatus(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
        xmlStreamWriter.writeStartElement(managementStatusElementName);
        xmlStreamWriter.writeCharacters(timeStamp == null ? "" : dateFormat.format(timeStamp));
		xmlStreamWriter.writeEndElement();
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}