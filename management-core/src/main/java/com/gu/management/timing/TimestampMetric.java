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