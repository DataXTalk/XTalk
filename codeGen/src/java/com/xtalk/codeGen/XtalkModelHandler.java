package com.xtalk.codeGen;

import com.xtalk.codeGen.objectType.Association;
import com.xtalk.codeGen.objectType.AssociationEnd;
import com.xtalk.codeGen.objectType.Attribute;
import com.xtalk.codeGen.objectType.Class;
import com.xtalk.codeGen.objectType.Package;
import com.xtalk.codeGen.objectType.TaggedValue;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Iterator;

public class XtalkModelHandler extends DefaultHandler {
    private static final String FILESEPARATOR = System.getProperty("file.separator");

    private Deque<Object> ownedMemberQueue = new ArrayDeque<Object>();

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("ownedMember".equals(localName)) {
            if (attributes.getValue("xmi:type").equals("uml:Package")) {
                Package m_package = new Package();
                m_package.setPackageID(attributes.getValue("xmi:id"));
                m_package.setAbstract(Boolean.parseBoolean(attributes.getValue("isAbstract")));
                m_package.setLeaf(Boolean.parseBoolean(attributes.getValue("isLeaf")));
                m_package.setName(attributes.getValue("name"));

                Iterator<Package> it = XtalkModelGenerator.PACKAGELIST.descendingIterator();
                while (it.hasNext()) {
                    Package pkg = it.next();
                    if (!pkg.isFinishedProcessing()) {
                        m_package.setNameSpace(pkg.getPackageID());
                        m_package.setNameSpacePath(pkg.getNameSpacePath() + FILESEPARATOR + m_package.getName());
                        break;
                    }
                }

                if (m_package.getNameSpace() == null) // means this package will be the first one to add to list
                    m_package.setNameSpacePath(m_package.getName());

                XtalkModelGenerator.PACKAGELIST.add(m_package);
                ownedMemberQueue.push(m_package);
            } else if (attributes.getValue("xmi:type").equals("uml:Class")) {
                Class m_class = new Class();
                m_class.setClassID(attributes.getValue("xmi:id"));
                m_class.setAbstract(Boolean.parseBoolean(attributes.getValue("isAbstract")));
                m_class.setLeaf(Boolean.parseBoolean(attributes.getValue("isLeaf")));
                m_class.setName(attributes.getValue("name"));
                m_class.setActive(Boolean.parseBoolean(attributes.getValue("isActive")));
                m_class.setVisibility(attributes.getValue("visibility"));
                m_class.setNameSpace(XtalkModelGenerator.PACKAGELIST.getLast().getPackageID());
                m_class.setOwnerPackage(XtalkModelGenerator.PACKAGELIST.getLast());

                XtalkModelGenerator.CLASSLIST.add(m_class);
                ownedMemberQueue.push(m_class);
            } else if (attributes.getValue("xmi:type").equals("uml:Enumeration")) {
                Class m_class = new Class();
                m_class.setClassID(attributes.getValue("xmi:id"));
                m_class.setAbstract(Boolean.parseBoolean(attributes.getValue("isAbstract")));
                m_class.setLeaf(Boolean.parseBoolean(attributes.getValue("isLeaf")));
                m_class.setName(attributes.getValue("name"));
                m_class.setActive(Boolean.parseBoolean(attributes.getValue("isActive")));
                m_class.setVisibility(attributes.getValue("visibility"));
                m_class.setNameSpace(XtalkModelGenerator.PACKAGELIST.getLast().getPackageID());
                m_class.setOwnerPackage(XtalkModelGenerator.PACKAGELIST.getLast());
                m_class.setEnumType(true);

                XtalkModelGenerator.CLASSLIST.add(m_class);
                ownedMemberQueue.push(m_class);
            } else if (attributes.getValue("xmi:type").equals("uml:Association")) {
                Association m_association = new Association();
                m_association.setAssociationID(attributes.getValue("xmi:id"));
                m_association.setAbstract(Boolean.parseBoolean(attributes.getValue("isAbstract")));
                m_association.setLeaf(Boolean.parseBoolean(attributes.getValue("isLeaf")));
                m_association.setLeaf(Boolean.parseBoolean(attributes.getValue("isDerived")));
                m_association.setName(attributes.getValue("name"));
                m_association.setContainingPackage(XtalkModelGenerator.PACKAGELIST.getLast());

                XtalkModelGenerator.ASSOCIATIONLIST.add(m_association);
                ownedMemberQueue.push(m_association);
            } else {
                //we need to add to ownedMemeberQueue for every owbedMemeber object, so that endElement method can "pop" it
                ownedMemberQueue.push(new Object());
            }
        } else if ("generalization".equals(localName)) {
            XtalkModelGenerator.CLASSLIST.lastElement().setParentClassID(attributes.getValue("general"));
        } else if ("appliedStereotype".equals(localName)) {
            if (attributes.getValue("xmi:value").equals("Class_datatype_id")){
                XtalkModelGenerator.CLASSLIST.lastElement().setDataType(true);
            }
        } else if ("ownedEnd".equals(localName)) {
            AssociationEnd m_associationEnd = new AssociationEnd();
            m_associationEnd.setAssociationEndID(attributes.getValue("xmi:id"));
            m_associationEnd.setAggregation(attributes.getValue("aggregation"));
            m_associationEnd.setName(attributes.getValue("name"));
            m_associationEnd.setType(attributes.getValue("type"));
            m_associationEnd.setNavigable(Boolean.parseBoolean(attributes.getValue("isNavigable")));

            XtalkModelGenerator.ASSOCIATIONLIST.lastElement().addAssociationEnd(m_associationEnd);
            m_associationEnd.setOwnerAssociation(XtalkModelGenerator.ASSOCIATIONLIST.lastElement());
            XtalkModelGenerator.ASSOCIATIONENDLIST.add(m_associationEnd);
        } else if ("lowerValue".equals(localName)) {
            XtalkModelGenerator.ASSOCIATIONENDLIST.lastElement().setMultiplicityRange_lower(attributes.getValue("value"));
            XtalkModelGenerator.ASSOCIATIONENDLIST.lastElement().setMultiplicityRange_upper(attributes.getValue("value"));
        } else if ("upperValue".equals(localName)) {
            XtalkModelGenerator.ASSOCIATIONENDLIST.lastElement().setMultiplicityRange_upper(attributes.getValue("value"));
        } else if ("ownedAttribute".equals(localName)) {
            Attribute m_attribute = new Attribute();
            m_attribute.setAttributeID(attributes.getValue("xmi:id"));
            m_attribute.setOwnerScope(attributes.getValue("ownerScope"));
            m_attribute.setAttributeName(attributes.getValue("name"));
            m_attribute.setType(attributes.getValue("type"));
            m_attribute.setVisibility(attributes.getValue("visibility"));

            XtalkModelGenerator.CLASSLIST.lastElement().addAttribute(m_attribute);
            XtalkModelGenerator.ATTRIBUTELIST.add(m_attribute);
        } else if ("ownedLiteral".equals(localName)) {
            if (attributes.getValue("xmi:type").equals("uml:EnumerationLiteral")){
                Attribute m_attribute = new Attribute();
                m_attribute.setAttributeID(attributes.getValue("xmi:id"));
                m_attribute.setAttributeName(attributes.getValue("name"));
                m_attribute.setVisibility(attributes.getValue("visibility"));
                m_attribute.setType("uml:EnumerationLiteral");

                XtalkModelGenerator.CLASSLIST.lastElement().addAttribute(m_attribute);
                XtalkModelGenerator.ATTRIBUTELIST.add(m_attribute);
            }
        } else if ("documentation".equals(localName)) {
            TaggedValue m_taggedValue = new TaggedValue();
            m_taggedValue.setValue(attributes.getValue("htmlBody"));
            m_taggedValue.setModelElement(attributes.getValue("xmi:id").replace("_documentation", ""));

            XtalkModelGenerator.TAGGEDVALUELIST.add(m_taggedValue);
        }

    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("ownedMember".equals(localName)) {
            Object inst = ownedMemberQueue.pop();
            if (inst instanceof Package) {
                ((Package) inst).setFinishedProcessing(true);
            }
        }

        if ("uml:Model".equals(qName)) {
            for (Class m_class : XtalkModelGenerator.CLASSLIST) {
                if (m_class.getParentClassID() != null) {
                    m_class.setParentClass(getClass(m_class.getParentClassID()));
                }
            }
        } 
    }

    public Class getClass(String classID) {
        for (Class m_class : XtalkModelGenerator.CLASSLIST) {
            if (classID.equals(m_class.getClassID())) {
                return m_class;
            }
        }
        return null;
    }
}
