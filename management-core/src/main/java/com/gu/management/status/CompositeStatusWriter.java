package com.gu.management.status;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Collections;
import java.util.List;


public class CompositeStatusWriter extends StatusWriter {

	private String wrappingElementName = "";

	private List<StatusWriter> childStatusWriters = Collections.emptyList();

	public CompositeStatusWriter() {
    }

	public void setElementName(String wrappingElementName) {
		this.wrappingElementName = wrappingElementName;
	}


	public void setChildStatusWriters(List<StatusWriter> childStatusWriters) {
		this.childStatusWriters = childStatusWriters;
	}

	@Override public void writeStatus(XMLStreamWriter xmlStreamWriter) throws XMLStreamException {
		xmlStreamWriter.writeStartElement(wrappingElementName);
		for (StatusWriter statusWriter : childStatusWriters) {
			statusWriter.writeStatus(xmlStreamWriter);
        }
	    xmlStreamWriter.writeEndElement();
    }

}