package com.xtalk.codeGen;

import static com.xtalk.codeGen.XtalkModelGenerator.manyToManySet;
import static com.xtalk.codeGen.XtalkModelGenerator.oneToOneSet;
import com.xtalk.codeGen.objectType.AssociationEnd;
import com.xtalk.codeGen.objectType.Attribute;
import com.xtalk.codeGen.objectType.Class;
import com.xtalk.codeGen.util.GeneratedClassID;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;


/**
 * Created by IntelliJ IDEA.
 * User: mzfan
 * Date: Apr 9, 2007
 * Time: 11:48:15 PM
 */
public class    XtalkModelMappingFileCreator {
    private File m_outputFile;
    private Class m_class;

    public XtalkModelMappingFileCreator(Class i_class, File i_outputFile) {
        m_outputFile = i_outputFile;
        m_class = i_class;
    }

    private void generateDomTree(Document i_doc) throws Exception {
        ////////////////////////
        //Creating the XML tree

        //create the root element and add it to the document
        String packagePath = m_class.getOwnerPackage().getNameSpacePath().replace(System.getProperty("file.separator").charAt(0), '.');
        Element rootNode = i_doc.createElement("hibernate-mapping");
        rootNode.setAttribute("package", packagePath);
        rootNode.setAttribute("schema", "designer");
        i_doc.appendChild(rootNode);

        Object typeId = new GeneratedClassID().getClassIdMap().get(packagePath + "." + m_class.getName());
        if (typeId == null) throw new Exception("There is no matched typeId found. Critical Error. Exiting...");

        //we add property first
        Element classNode = createPropertyNodes(i_doc, typeId);

        //then we add associations
        createAssociationNodes(i_doc, classNode);

        rootNode.appendChild(classNode);

    }

    private void createAssociationNodes(Document i_doc, Element classNode) {
        for (AssociationEnd m_asso_end : XtalkModelGenerator.ASSOCIATIONENDLIST) {
            if (m_asso_end.getType().equals(m_class.getClassID())) {
                AssociationEnd other_end = m_asso_end.getOwnerAssociation().getOtherAssociationEnd(m_asso_end);
                Class m_other_class = XtalkModelJavaClass.getClass(other_end.getType());
                //we handle unidirectional association first
                if (!m_asso_end.isNavigable() && other_end.isNavigable()) {
                    //one-to-one (no join table)
                    if (other_end.getMultiplicityRange_upper().equals("1") &&
                            m_asso_end.getMultiplicityRange_upper().equals("1")) {
                        Element manyToOneNode = i_doc.createElement("many-to-one");
                        manyToOneNode.setAttribute("name", other_end.getName());
                        manyToOneNode.setAttribute("column", m_other_class.getName() + "_" + other_end.getName() + "_Id");
                        manyToOneNode.setAttribute("not-null", "false");
                        manyToOneNode.setAttribute("unique", "true");
                        manyToOneNode.setAttribute("cascade", "persist,merge,save-update");

                        classNode.appendChild(manyToOneNode);
                    }
                    //many-to-one  (no join table)
                    if (other_end.getMultiplicityRange_upper().equals("1") &&
                            !m_asso_end.getMultiplicityRange_upper().equals("1")) {
                        Element manyToOneNode = i_doc.createElement("many-to-one");
                        manyToOneNode.setAttribute("name", other_end.getName());
                        manyToOneNode.setAttribute("column", m_other_class.getName() + "_" + other_end.getName() + "_Id");
                        manyToOneNode.setAttribute("not-null", "false");

                        classNode.appendChild(manyToOneNode);
                    }
                    //one-to-many  (join table)
                    if (!other_end.getMultiplicityRange_upper().equals("1") &&
                            m_asso_end.getMultiplicityRange_upper().equals("1")) {
                        Element setNode = i_doc.createElement("set");
                        setNode.setAttribute("table", "X_" + m_asso_end.getName() + other_end.getName());
                        setNode.setAttribute("name", other_end.getName());
                        setNode.setAttribute("cascade", "persist,merge,save-update");

                        Element keyNode = i_doc.createElement("key");
                        keyNode.setAttribute("column", m_class.getName() + "_" + m_asso_end.getName() + "_Id");

                        Element manyToManyNode = i_doc.createElement("many-to-many");
                        manyToManyNode.setAttribute("column", m_other_class.getName() + "Id");
                        manyToManyNode.setAttribute("unique", "true");
                        manyToManyNode.setAttribute("class", m_other_class.getOwnerPackage().getNameSpacePath().replace(System.getProperty("file.separator").charAt(0), '.')
                                + "." + m_other_class.getName());

                        setNode.appendChild(keyNode);
                        setNode.appendChild(manyToManyNode);

                        classNode.appendChild(setNode);
                    }
                    //many-to-many (join table)
                    if (!other_end.getMultiplicityRange_upper().equals("1") &&
                            !m_asso_end.getMultiplicityRange_upper().equals("1")) {
                        Element setNode = i_doc.createElement("set");
                        setNode.setAttribute("table", "X_" + m_asso_end.getName() + other_end.getName());
                        setNode.setAttribute("name", other_end.getName());

                        Element keyNode = i_doc.createElement("key");
                        keyNode.setAttribute("column", m_class.getName() + "_" + m_asso_end.getName() + "_Id");

                        Element manyToManyNode = i_doc.createElement("many-to-many");
                        manyToManyNode.setAttribute("column", m_other_class.getName() + "_" + other_end.getName() + "_Id");
                        manyToManyNode.setAttribute("class", m_other_class.getOwnerPackage().getNameSpacePath().replace(System.getProperty("file.separator").charAt(0), '.')
                                + "." + m_other_class.getName());

                        setNode.appendChild(keyNode);
                        setNode.appendChild(manyToManyNode);

                        classNode.appendChild(setNode);
                    }
                }
                //bidirectional associations
                if (m_asso_end.isNavigable() && other_end.isNavigable()) {
                    //many-to-one  (no join table)
                    if (other_end.getMultiplicityRange_upper().equals("1") &&
                            !m_asso_end.getMultiplicityRange_upper().equals("1")) {
                        Element manyToOneNode = i_doc.createElement("many-to-one");
                        manyToOneNode.setAttribute("name", other_end.getName());
                        manyToOneNode.setAttribute("column", m_other_class.getName() + "_" + other_end.getName() + "_Id");

                        //special handling for classifier-feature association
                        if (other_end.getName().equalsIgnoreCase("owner"))
                            manyToOneNode.setAttribute("not-null", "true");
                        else
                            manyToOneNode.setAttribute("not-null", "false");

                        classNode.appendChild(manyToOneNode);
                    }
                    //one-to-many  (no join table)
                    if (!other_end.getMultiplicityRange_upper().equals("1") &&
                            m_asso_end.getMultiplicityRange_upper().equals("1")) {
                        Element setNode = i_doc.createElement("set");
                        setNode.setAttribute("inverse", "true");
                        setNode.setAttribute("name", other_end.getName());

                        //special handling for classifier-feature association
                        if (other_end.getName().equalsIgnoreCase("feature"))
                            setNode.setAttribute("cascade", "all,delete-orphan");
                        else
                            setNode.setAttribute("cascade", "persist,merge,save-update");

                        Element keyNode = i_doc.createElement("key");
                        keyNode.setAttribute("column", m_class.getName() + "_" + m_asso_end.getName() + "_Id");

                        Element manyToManyNode = i_doc.createElement("one-to-many");
                        manyToManyNode.setAttribute("class", m_other_class.getOwnerPackage().getNameSpacePath().replace(System.getProperty("file.separator").charAt(0), '.')
                                + "." + m_other_class.getName());

                        setNode.appendChild(keyNode);
                        setNode.appendChild(manyToManyNode);

                        classNode.appendChild(setNode);
                    }
                    //one-to-one (no joinn table)
                    if (other_end.getMultiplicityRange_upper().equals("1") &&
                            m_asso_end.getMultiplicityRange_upper().equals("1")) {
                        if (oneToOneSet.contains(other_end.getAssociationEndID())) {
                            Element manyToOneNode = i_doc.createElement("many-to-one");
                            manyToOneNode.setAttribute("name", other_end.getName());
                            manyToOneNode.setAttribute("column", m_other_class.getName() + "_" + other_end.getName() + "_Id");
                            manyToOneNode.setAttribute("unique", "true");
                            manyToOneNode.setAttribute("not-null", "false");
                            manyToOneNode.setAttribute("cascade", "persist,merge,save-update");

                            classNode.appendChild(manyToOneNode);
                        } else {
                            Element oneToOneNode = i_doc.createElement("one-to-one");
                            oneToOneNode.setAttribute("name", other_end.getName());
                            oneToOneNode.setAttribute("property-ref", m_asso_end.getName());
                            oneToOneNode.setAttribute("cascade", "persist,merge,save-update");

                            classNode.appendChild(oneToOneNode);
                        }
                        oneToOneSet.add(m_asso_end.getAssociationEndID());
                    }
                    //many-to-many  (join table)
                    if (!other_end.getMultiplicityRange_upper().equals("1") &&
                            !m_asso_end.getMultiplicityRange_upper().equals("1")) {
                        Element setNode = i_doc.createElement("set");
                        setNode.setAttribute("table", "X_" + m_asso_end.getName() + other_end.getName());
                        setNode.setAttribute("name", other_end.getName());

                        Element keyNode = i_doc.createElement("key");
                        keyNode.setAttribute("column", m_class.getName() + "_" + m_asso_end.getName() + "_Id");

                        Element manyToManyNode = i_doc.createElement("many-to-many");
                        manyToManyNode.setAttribute("column", m_other_class.getName() + "_" + other_end.getName() + "_Id");
                        manyToManyNode.setAttribute("class", m_other_class.getOwnerPackage().getNameSpacePath().replace(System.getProperty("file.separator").charAt(0), '.')
                                + "." + m_other_class.getName());

                        if (manyToManySet.contains(other_end.getAssociationEndID())) {
                            setNode.setAttribute("inverse", "true");
                            setNode.setAttribute("table", "X_" + m_asso_end.getName() + other_end.getName());
                        } else {
                            setNode.setAttribute("table", "X_" + other_end.getName() + m_asso_end.getName());
                        }


                        manyToManySet.add(m_asso_end.getAssociationEndID());


                        setNode.appendChild(keyNode);
                        setNode.appendChild(manyToManyNode);

                        classNode.appendChild(setNode);
                    }
                }
            }
        }
    }

    private Element createPropertyNodes(Document i_doc, Object typeId) {
        Element classNode;
        if (m_class.getName().equals("Element")) {
            classNode = i_doc.createElement("class");
            classNode.setAttribute("name", m_class.getName());
            classNode.setAttribute("table", "X_ELEMENT");

            addPropertyNodes(i_doc, typeId, classNode);
        } else {
            classNode = i_doc.createElement("joined-subclass");
            classNode.setAttribute("name", m_class.getName());
       
            classNode.setAttribute("extends", m_class.getParentClass().getOwnerPackage().getNameSpacePath().replace(System.getProperty("file.separator").charAt(0), '.')
                    + "." + m_class.getParentClass().getName());

            classNode.setAttribute("table", "X_" + m_class.getName());

            Element keyNode = i_doc.createElement("key");
            keyNode.setAttribute("column", m_class.getName() + "Id");

            classNode.appendChild(keyNode);

            addPropertyNodes(i_doc, typeId, classNode);

        }
        return classNode;
    }

    private void addPropertyNodes(Document i_doc, Object typeId, Element parentNode) {
        for (Attribute m_attr : m_class.getAttributeList()) {
            Class m_class = XtalkModelJavaClass.getClass(m_attr.getType());
            String m_attrType = getHibernateTypeInfo(m_class);
            String m_attrName = m_attr.getAttributeName();
            Element propertyNode;
            if (m_attrName.equalsIgnoreCase("id"))
                propertyNode = i_doc.createElement("id");
            else if (m_attrName.equalsIgnoreCase("optlock")) {
                propertyNode = i_doc.createElement("version");
                propertyNode.setAttribute("unsaved-value", "negative");
            } else
                propertyNode = i_doc.createElement("property");

            propertyNode.setAttribute("name", m_attrName);
            propertyNode.setAttribute("column", m_attrName + "_" + typeId.toString());
            if (m_class.isEnumType()) {
                Element typeNode = i_doc.createElement("type");
                typeNode.setAttribute("name", "com.xtalk.ext.hibernate.usertype.EnumUserType");
                propertyNode.appendChild(typeNode);
                propertyNode.setAttribute("length", "20");

                Element paramNode = i_doc.createElement("param");
                paramNode.setAttribute("name", "enumClassName");
                typeNode.appendChild(paramNode);

                Text textNode = i_doc.createTextNode(m_class.getOwnerPackage().getNameSpacePath().replace(System.getProperty("file.separator").charAt(0), '.') + "." + m_class.getName());
                paramNode.appendChild(textNode);
            } else {
                if (m_attrType.equals("string")){
                    propertyNode.setAttribute("type", "string");
                    propertyNode.setAttribute("length", "256");
                }
                if (m_attrType.equals("smallstring")){
                    propertyNode.setAttribute("type", "string");
                    propertyNode.setAttribute("length", "40");
                }
                if (m_attrType.equals("bigstring")){
                    propertyNode.setAttribute("type", "string");
                    propertyNode.setAttribute("length", "1024");
                }

                if (m_attrName.equalsIgnoreCase("id")) {
                    propertyNode.setAttribute("type", m_attrType);

                    Element genNode = i_doc.createElement("generator");
                    genNode.setAttribute("class", "org.hibernate.id.enhanced.TableGenerator");

                    Element paramNode1 = i_doc.createElement("param");
                    paramNode1.setAttribute("name", "optimizer");
                    Text paramValueNode1 = i_doc.createTextNode("hilo");
                    paramNode1.appendChild(paramValueNode1);
                    genNode.appendChild(paramNode1);

                    Element paramNode2 = i_doc.createElement("param");
                    paramNode2.setAttribute("name", "initial_value");
                    Text paramValueNode2 = i_doc.createTextNode("1");
                    paramNode2.appendChild(paramValueNode2);
                    genNode.appendChild(paramNode2);

                    Element paramNode3 = i_doc.createElement("param");
                    paramNode3.setAttribute("name", "incremental_size");
                    Text paramValueNode3 = i_doc.createTextNode("100");
                    paramNode3.appendChild(paramValueNode3);
                    genNode.appendChild(paramNode3);

                    propertyNode.appendChild(genNode);
                }

                if (m_attrName.equalsIgnoreCase("typeId")) {
                    propertyNode.setAttribute("type", m_attrType);

                    propertyNode.setAttribute("index", "IDX_TYPEID");
                }
            }
            parentNode.appendChild(propertyNode);
        }

    }

    public void writeClassToMappingFile() {
        try {
            Document m_doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            generateDomTree(m_doc);

            Source source = new DOMSource(m_doc);

            Result result = new StreamResult(m_outputFile);

            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//Hibernate/Hibernate Mapping DTD 3.0//EN");
            xformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd");
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

    private String getHibernateTypeInfo(Class m_class) {
        String m_typename = m_class.getName();
        if (m_typename.equals("Integer")) {
            return "integer";
        } else if (m_typename.equals("Long")) {
            return "long";
        } else if (m_typename.equals("Float")) {
            return "float";
        } else if (m_typename.equals("Boolean")) {
            return "boolean";
        } else if (m_typename.equals("Time")) {
            return "timestamp";
        } else if (m_typename.equals("String")) {
            return "string";
        } else if (m_typename.equals("SmallString")) {
            return "smallstring";
        } else if (m_typename.equals("BigString")) {
            return "bigstring";
        } else {
            return m_typename;
        }
    }

}
