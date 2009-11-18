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
