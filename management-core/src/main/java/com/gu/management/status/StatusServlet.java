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

package com.gu.management.status;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.util.Collection;

public class StatusServlet extends HttpServlet {
    private XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
    private Collection<StatusWriter> statusWriters;

    public StatusServlet(Collection<StatusWriter> statusWriters) {
        this.statusWriters = statusWriters;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/xml");
        XMLStreamWriter writer = null;
        try {
            writer = xmlOutputFactory.createXMLStreamWriter(response.getWriter());

            writer.writeStartElement("status");
            for (StatusWriter statusWriter : statusWriters) {
                statusWriter.writeStatus(writer);
            }
            writer.writeEndElement();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception e) {
                    // nowt to do
                }
            }
        }
    }
}
