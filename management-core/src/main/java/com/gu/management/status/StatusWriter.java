package com.gu.management.status;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;


public abstract class StatusWriter {
	public abstract void writeStatus(XMLStreamWriter xmlStreamWriter) throws XMLStreamException;

	protected void writeElement(XMLStreamWriter writer, String name, String value) throws XMLStreamException {
		writer.writeStartElement(name);
		writer.writeCharacters(value);
		writer.writeEndElement();
	}
}