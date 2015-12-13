package com.xtalk.codeGen;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: mzfan
 * Date: Apr 20, 2007
 * Time: 10:54:25 PM
 */
public class XtalkModelConfigFileCreator {
    private File m_outputFile;

    public XtalkModelConfigFileCreator(File m_outputFile) {
        this.m_outputFile = m_outputFile;
    }

    private void generateDomTree(Document i_doc) throws Exception {
        Element rootElement = i_doc.createElement("persistence");
        rootElement.setAttribute("xmlns", "http://java.sun.com/xml/ns/persistence");
        rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        rootElement.setAttribute("xsi:schemaLocation", "http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd");
        rootElement.setAttribute("version", "1.0");
        i_doc.appendChild(rootElement);

        Element persistentUnitNode = i_doc.createElement("persistence-unit");
        persistentUnitNode.setAttribute("name", "xtalk");
        persistentUnitNode.setAttribute("transaction-type", "RESOURCE_LOCAL");
        rootElement.appendChild(persistentUnitNode);

        Element providerNode = i_doc.createElement("provider");
        persistentUnitNode.appendChild(providerNode);

        Text providerValue = i_doc.createTextNode("org.hibernate.ejb.HibernatePersistence");
        providerNode.appendChild(providerValue);

        //mapping file config
        for (String mappingFileName : XtalkModelGenerator.FULLCLASSNAMELIST){
            Element classElement = i_doc.createElement("class");
            Text className = i_doc.createTextNode(mappingFileName);
            classElement.appendChild(className);

            persistentUnitNode.appendChild(classElement);
        }
        
        Element propertiesNode = i_doc.createElement("properties");
        persistentUnitNode.appendChild(propertiesNode);

        //debug config
        Element showSQLNode = i_doc.createElement("property");
        showSQLNode.setAttribute("name", "hibernate.show_sql");
        showSQLNode.setAttribute("value", "true");
        propertiesNode.appendChild(showSQLNode);

        //default schema config
        Element schemaNode = i_doc.createElement("property");
        schemaNode.setAttribute("name", "hibernate.default_schema");
        schemaNode.setAttribute("value", "designer");
        propertiesNode.appendChild(schemaNode);

        //dialect config
        Element dialectNode = i_doc.createElement("property");
        dialectNode.setAttribute("name", "hibernate.dialect");
        dialectNode.setAttribute("value", "org.hibernate.dialect.DerbyDialect");
        propertiesNode.appendChild(dialectNode);

        //jdbc batch config
        Element batchNode = i_doc.createElement("property");
        batchNode.setAttribute("name", "hibernate.jdbc.batch_size");
        batchNode.setAttribute("value", "50");
        propertiesNode.appendChild(batchNode);

        //driver class config
        Element driverNode = i_doc.createElement("property");
        driverNode.setAttribute("name", "hibernate.connection.driver_class");
        driverNode.setAttribute("value", "org.apache.derby.jdbc.EmbeddedDriver");
        propertiesNode.appendChild(driverNode);

        //connection URL config
        Element urlNode = i_doc.createElement("property");
        urlNode.setAttribute("name", "hibernate.connection.url");
        urlNode.setAttribute("value", "jdbc:derby:db/xtalk;create=true");
        propertiesNode.appendChild(urlNode);

        //connection connection-provider config
        Element cproviderNode = i_doc.createElement("property");
        cproviderNode.setAttribute("name", "hibernate.connection.provider_class");
        cproviderNode.setAttribute("value", "org.hibernate.connection.C3P0ConnectionProvider");
        propertiesNode.appendChild(cproviderNode);

        //connection c3p0 config
        Element c3p0Node1 = i_doc.createElement("property");
        c3p0Node1.setAttribute("name", "hibernate.c3p0.min_size");
        c3p0Node1.setAttribute("value", "5");
        propertiesNode.appendChild(c3p0Node1);

        //connection c3p0 config
        Element c3p0Node2 = i_doc.createElement("property");
        c3p0Node2.setAttribute("name", "hibernate.c3p0.max_size");
        c3p0Node2.setAttribute("value", "20");
        propertiesNode.appendChild(c3p0Node2);

        //connection c3p0 config
        Element c3p0Node3 = i_doc.createElement("property");
        c3p0Node3.setAttribute("name", "hibernate.c3p0.timeout");
        c3p0Node3.setAttribute("value", "1800");
        propertiesNode.appendChild(c3p0Node3);

        //connection c3p0 config
        Element c3p0Node4 = i_doc.createElement("property");
        c3p0Node4.setAttribute("name", "hibernate.c3p0.max_statements");
        c3p0Node4.setAttribute("value", "50");
        propertiesNode.appendChild(c3p0Node4);

    }

    public void createConfigFile() {
        try {
            Document m_doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            generateDomTree(m_doc);

            Source source = new DOMSource(m_doc);

            Result result = new StreamResult(m_outputFile);

            Transformer xformer = TransformerFactory.newInstance().newTransformer();

            xformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            xformer.setOutputProperty(OutputKeys.INDENT, "yes");

            xformer.transform(source, result);

        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
