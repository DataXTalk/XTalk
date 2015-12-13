package com.xtalk.codeGen;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.FileReader;
import java.io.IOException;


public class XtalkModelParser {

    static String XTALKMODELFILE = "model" + System.getProperty("file.separator") + "CrossTalk.xml";

    XMLReader producer;
    XtalkModelHandler consumer;

    public XtalkModelParser() {
        try {
            producer = XMLReaderFactory.createXMLReader();
        } catch (SAXException e) {
            System.err.println("Can't get parser, check configuration: " + e.getMessage());
        }

        try {
            // Get a consumer for all the parser events
            consumer = new XtalkModelHandler();

            // Connect the most important standard handler
            producer.setContentHandler(consumer);

            // Arrange error handling
            producer.setErrorHandler(consumer);
        } catch (Exception e) {
            System.err.println("Can't set up consumers: " + e.getMessage());
        }
    }

    public void Process() {
        try {
            producer.parse(new InputSource(new FileReader(XTALKMODELFILE)));
        } catch (IOException e) {
            System.err.println("I/O error: ");
            e.printStackTrace();
        } catch (SAXException e) {
            System.err.println("Parsing error: ");
            e.printStackTrace();
        }
    }


}
