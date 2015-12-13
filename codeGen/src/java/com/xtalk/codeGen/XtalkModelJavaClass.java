package com.xtalk.codeGen;

import static com.xtalk.codeGen.XtalkModelGenerator.manyToManySet;
import static com.xtalk.codeGen.XtalkModelGenerator.oneToOneSet;
import com.xtalk.codeGen.objectType.AssociationEnd;
import com.xtalk.codeGen.objectType.Attribute;
import com.xtalk.codeGen.objectType.Class;
import com.xtalk.codeGen.objectType.TaggedValue;
import com.xtalk.codeGen.util.GeneratedClassID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class XtalkModelJavaClass {
    private String classID;
    private StringBuilder classPackage;
    private StringBuilder classHeader;
    private StringBuilder classFooter;

    private String packagePath;

    private Class wrapperClass;

    private Object typeId;

    private StringBuilder classComment_block;
    private StringBuilder standardImport_block;

    private StringBuilder attribute_block;
    private StringBuilder association_block;

    private StringBuilder standardMethods_block;

    private String customizedMethod_block;
    private String customizedImport_block;

    private StringBuilder constructor_block;

    private Set<String> stdImport_STMT = new HashSet<String>();
    private List<StringBuilder> standardMethods_list = new ArrayList<StringBuilder>();

    public static String NEWLINE = System.getProperty("line.separator");

    public XtalkModelJavaClass(Class wrapperClass) {
        this.wrapperClass = wrapperClass;
        this.classID = wrapperClass.getClassID();

        this.packagePath = wrapperClass.getOwnerPackage().getNameSpacePath().replace(System.getProperty("file.separator").charAt(0), '.');
        this.typeId = new GeneratedClassID().getClassIdMap().get(packagePath + "." + wrapperClass.getName());
    }

    public void setupClassPackage() {
        this.classPackage = new StringBuilder("package ").
                append(packagePath).
                append(";");
    }

    public void setupClassHeader() {
        if (!wrapperClass.isEnumType()) {
            StringBuilder annotation = new StringBuilder("@Entity ").append(NEWLINE).
                    append("@Table(name=\"X_").append(wrapperClass.getName()).append("\", schema=\"designer\")").append(NEWLINE);

            if (wrapperClass.getParentClass() == null) { //it refers to Top class: Element
                annotation.append("@Inheritance(strategy=InheritanceType.JOINED)").append(NEWLINE);
                this.classHeader = new StringBuilder(annotation).append("public class ").append(wrapperClass.getName());

                stdImport_STMT.add("import java.io.Serializable;");

                this.classHeader.append(" implements Serializable ").append(" { ");
            } else {
                annotation.append("@PrimaryKeyJoinColumn(name=\"").append(wrapperClass.getName()).append("Id\")").append(NEWLINE);
                this.classHeader = new StringBuilder(annotation).append("public class ").append(wrapperClass.getName());
                this.classHeader.append(" extends ").append(wrapperClass.getParentClass().getName()).append(" { ");
            }

            //all classes needs to import javax.persistence annotation classes
            stdImport_STMT.add("import javax.persistence.*;");
            //stdImport_STMT.add("import javax.persistence.Table;");

            if (wrapperClass.getParentClass() != null) {
                if (!wrapperClass.getOwnerPackage().getPackageID().equals(wrapperClass.getParentClass().getOwnerPackage().getPackageID())) {
                    stdImport_STMT.add("import " + wrapperClass.getParentClass().getOwnerPackage().getNameSpacePath().replace(System.getProperty("file.separator").charAt(0), '.')
                            + "." + wrapperClass.getParentClass().getName() + ";");
                }
            }
        } else {
            this.classHeader = new StringBuilder("public enum ").
                    append(wrapperClass.getName()).
                    append(" { ");
        }
    }

    public void setupConstructor_block() {
        constructor_block = new StringBuilder();
        if (!wrapperClass.isEnumType()) {
            if (!wrapperClass.getName().equals("Element")) {
                constructor_block.append("\tpublic ").
                        append(wrapperClass.getName()).
                        append(" () {").
                        append(NEWLINE).
                        append("\t\tthis.typeId = ").append(typeId).
                        append(";").append(NEWLINE).
                        append("\t}");
                if (wrapperClass.getName().equals("ModelElement")) { //ModelElement constructor
                    constructor_block.append(NEWLINE).append(NEWLINE).append("\tpublic ").
                            append(wrapperClass.getName()).
                            append(" (String i_name) {").
                            append(NEWLINE).append("\t\t").append("this.name = i_name;").
                            append(NEWLINE).append("\t\tthis.typeId = ").append(typeId).
                            append(";").append(NEWLINE).
                            append("\t}");
                } else {
                    if (isClassInheritedFromModelElement(wrapperClass)) {
                        constructor_block.append(NEWLINE).append(NEWLINE).append("\tpublic ").
                                append(wrapperClass.getName()).
                                append(" (String i_name) {").
                                append(NEWLINE).append("\t\t").append("super(i_name);").
                                append(NEWLINE).append("\t\tthis.typeId = ").append(typeId).
                                append(";").append(NEWLINE).
                                append("\t}");
                    }
                }

            } else { //Element constructor
                constructor_block.append("\tpublic ").
                        append(wrapperClass.getName()).
                        append(" () {").append(NEWLINE).append(NEWLINE).
                        append("\t\tthis.internalUUID = java.util.UUID.randomUUID().toString();").append(NEWLINE).
                        append(NEWLINE).
                        append("\t\tthis.whenCreated = new Date();").append(NEWLINE).
                        append(NEWLINE).
                        append("\t\tthis.typeId = ").append(typeId).
                        append(";").append(NEWLINE).append(NEWLINE).append("\t}");
                //create a hashCode() function
                StringBuilder m_hashFunction = new StringBuilder();
                m_hashFunction.append("\tpublic int hashCode() {").append(NEWLINE).
                        append("\t\treturn internalUUID.hashCode();").append(NEWLINE).
                        append("\t}").append(NEWLINE);
                this.standardMethods_list.add(m_hashFunction);
                //create an equal function
                StringBuilder m_equalFunction = new StringBuilder();
                m_equalFunction.append("\tpublic boolean equals(Object o) {").append(NEWLINE).
                        append("\t\treturn (o == this || (o instanceof Element &&").append(NEWLINE).
                        append("\t\t\t\tinternalUUID.equals(((Element)o).getInternalUUID())));").append(NEWLINE).
                        append("\t}").append(NEWLINE);
                this.standardMethods_list.add(m_equalFunction);
            }
        }
    }

    public void setupStandardMethods_block() {
        standardMethods_block = new StringBuilder();
        for (StringBuilder method : standardMethods_list) {
            standardMethods_block.append(method).append(NEWLINE);
        }
    }

    public void setupClassFooter() {
        this.classFooter = new StringBuilder("}");
    }

    public void setupClassComment_block() {
        this.classComment_block = new StringBuilder(getDocument(wrapperClass.getClassID()));
    }

    public void setupCustomizedImport_block() {
        this.customizedImport_block = wrapperClass.getCustomizedImports();
    }

    public void setupStandardImport_block() {
        standardImport_block = new StringBuilder();
        List<String> import_stmt = new ArrayList<String>(stdImport_STMT);
        Collections.sort(import_stmt);
        for (String m_import : import_stmt) {
            standardImport_block.append(m_import).append(NEWLINE);
        }
    }

    public void setupAttribute_block() {
        this.attribute_block = new StringBuilder();
        for (Attribute m_attr : wrapperClass.getAttributeList()) {
            attribute_block.append("\t/**").
                    append(NEWLINE).
                    append("\t").append(getDocument(m_attr.getAttributeID())).
                    append(NEWLINE).
                    append("\t*/").
                    append(NEWLINE);
            if (!wrapperClass.isEnumType()) {
                Class m_class = getClass(m_attr.getType());

                String m_attrType = getJavaTypeInfo(m_class);
                String m_attrName = m_attr.getAttributeName();
                String attributeType;
                if (m_attrType.equalsIgnoreCase("BigString") || m_attrType.equalsIgnoreCase("SmallString")
                        ||m_attrType.equalsIgnoreCase("Clob"))
                    attributeType = "String";
                else if(m_attrType.equalsIgnoreCase("Blob"))
                    attributeType = "Byte[]";
                else
                    attributeType = m_attrType;

                attribute_block.append("\tprotected ").append(attributeType).
                        append(" ").
                        append(m_attrName).
                        append(";").
                        append(NEWLINE).append(NEWLINE);
                buildSetterGetterMethods(m_attrType, m_attrName, m_class.isEnumType());
            } else {
                String m_attrName = m_attr.getAttributeName();
                attribute_block.append("\t").
                        append(m_attrName).
                        append(",").
                        append(NEWLINE).append(NEWLINE);
            }
        }
    }

    public void setupAssociation_block() {
        this.association_block = new StringBuilder("\t//////////Association Attributes//////////").append(NEWLINE);
        for (AssociationEnd m_asso_end : XtalkModelGenerator.ASSOCIATIONENDLIST) {
            if (m_asso_end.getType().equals(this.classID)) {
                AssociationEnd other_end = m_asso_end.getOwnerAssociation().getOtherAssociationEnd(m_asso_end);
                if (other_end.isNavigable()) {
                    Class type_class = getClass(other_end.getType());
                    addTypeToSTDImport(type_class);
                    association_block.append("\t/**").
                            append(NEWLINE).
                            append("\t").append(getDocument(m_asso_end.getOwnerAssociation().getAssociationID())).
                            append(NEWLINE).
                            append("\t*/").
                            append(NEWLINE);
                    if (other_end.getMultiplicityRange_upper().equals("1")) {
                        association_block.append("\tprotected ").append(type_class.getName()).
                                append(" ").
                                append(other_end.getName()).
                                append(";").
                                append(NEWLINE).append(NEWLINE);
                    } else {
                        association_block.append("\tprotected ").append("Set<").
                                append(type_class.getName()).append("> ").
                                append(other_end.getName()).
                                append(" = new HashSet<").append(type_class.getName()).append(">()").
                                append(";").
                                append(NEWLINE).append(NEWLINE);
                        stdImport_STMT.add("import java.util.Set;");
                        stdImport_STMT.add("import java.util.HashSet;");
                    }
                    buildAssociationMethods(type_class, m_asso_end, other_end);
                    buildOptionalMethods(type_class.getName(), m_asso_end, other_end);
                }
            }
        }
        association_block.append("\t////////End of Association Attributes///////").append(NEWLINE);
    }

    public void setupCustomizedMethod_block() {
        this.customizedMethod_block = wrapperClass.getCustomizedMethods();
    }


    public StringBuilder getAssociation_block() {
        return association_block;
    }

    public StringBuilder getClassHeader() {
        return classHeader;
    }

    public StringBuilder getStandardImport_block() {
        return standardImport_block;
    }

    public StringBuilder getClassFooter() {
        return classFooter;
    }

    public StringBuilder getAttribute_block() {
        return attribute_block;
    }

    public String getCustomizedMethod_block() {
        String m_header = "\t/*****************************************************" + NEWLINE
                + "\t*   Begin of customized methods                      " + NEWLINE
                + "\t*                                                    " + NEWLINE
                + "\t***********Begin Methods*************************/" + NEWLINE;

        String m_footer = "\t/********End Methods********************************" + NEWLINE
                + "\t*   End of customized methods                        " + NEWLINE
                + "\t*                                                    " + NEWLINE
                + "\t*****************************************************/" + NEWLINE;

        return m_header + (customizedMethod_block == null ? "" : customizedMethod_block) + m_footer;
    }

    public String getCustomizedImport_block() {
        String m_header = "/*****************************************************" + NEWLINE
                + "*   Begin of customized imports                      " + NEWLINE
                + "*                                                    " + NEWLINE
                + "********Begin Imports********************************/" + NEWLINE;

        String m_footer = "/*********End Imports**************************" + NEWLINE
                + "*   End of customized imports                        " + NEWLINE
                + "*                                                    " + NEWLINE
                + "******************************************************/";

        return m_header + (customizedImport_block == null ? "" : customizedImport_block) + m_footer;
    }

    public StringBuilder getClassComment_block() {
        return new StringBuilder("/**").
                append(NEWLINE).
                append(classComment_block).
                append(NEWLINE).
                append("*/");
    }

    public Class getWrapperClass() {
        return wrapperClass;
    }

    public String getClassID() {
        return classID;
    }

    public StringBuilder getConstructor_block() {
        return constructor_block;
    }

    public StringBuilder getStandardMethods_block() {
        return standardMethods_block;
    }

    public StringBuilder getClassPackage() {
        return classPackage;
    }


    private String getJavaTypeInfo(Class m_class) {
        if (!m_class.isDataType()) {
            addTypeToSTDImport(m_class);
        }

        String m_typename = m_class.getName();
        if (m_typename.equalsIgnoreCase("Integer")) {
            return "int";
        } else if (m_typename.equalsIgnoreCase("Long")) {
            return "Long";
        } else if (m_typename.equalsIgnoreCase("Float")) {
            return "float";
        } else if (m_typename.equalsIgnoreCase("Boolean")) {
            return "boolean";
        } else if (m_typename.equalsIgnoreCase("Time")) {
            stdImport_STMT.add("import java.util.Date;");
            return "Date";
        } else if (m_typename.equalsIgnoreCase("String")) {
            return "String";
        } else if (m_typename.equalsIgnoreCase("SmallString")) {
            return "SmallString";
        } else if (m_typename.equalsIgnoreCase("BigString")) {
            return "BigString";
        } else if (m_typename.equalsIgnoreCase("Clob")) {
            return "Clob";
        } else if (m_typename.equalsIgnoreCase("Blob")) {
            return "Blob";
        } else {
            return m_typename;
        }
    }

    private void addTypeToSTDImport(Class m_class) {
        if (!wrapperClass.getOwnerPackage().getPackageID().equals(m_class.getOwnerPackage().getPackageID())) {
            stdImport_STMT.add("import " + m_class.getOwnerPackage().getNameSpacePath().replace(System.getProperty("file.separator").charAt(0), '.')
                    + "." + m_class.getName() + ";");
        }
    }

    public static String getDocument(String elementID) {
        for (TaggedValue tv : XtalkModelGenerator.TAGGEDVALUELIST) {
            if (tv.getModelElement().equals(elementID)) {
                return tv.getValue();
            }
        }
        return "No document";
    }

    public static Class getClass(String classID) {
        for (Class m_class : XtalkModelGenerator.CLASSLIST) {
            if (classID.equals(m_class.getClassID())) {
                return m_class;
            }
        }
        return null;
    }

    public static boolean isClassInheritedFromModelElement(Class m_class) {
        return m_class.getParentClass() != null && (m_class.getParentClass().getName().equals("ModelElement") ||
                isClassInheritedFromModelElement(m_class.getParentClass()));
    }

    protected void buildSetterGetterMethods(String attrType, String attrName, boolean isEnumType) {
        StringBuilder upper_attrName = new StringBuilder(attrName);
        upper_attrName.setCharAt(0, Character.toUpperCase(attrName.charAt(0)));
        StringBuilder annotation = new StringBuilder();
        String attributeType;
        if (attrType.equalsIgnoreCase("BigString") || attrType.equalsIgnoreCase("SmallString") ||
                attrType.equalsIgnoreCase("Clob"))
            attributeType = "String";
        else if (attrType.equalsIgnoreCase("Blob"))
            attributeType = "Byte[]";
        else
            attributeType = attrType;

        //get method
        if (isEnumType) {
            annotation.append("\t@Enumerated(EnumType.STRING)").append(NEWLINE);
            annotation.append("\t@Basic(fetch = FetchType.EAGER)").append(NEWLINE).
                        append("\t@Column(name=\"").append(attrName).append("_").append(typeId).append("\", length=40)").append(NEWLINE);
        } else {
            if (attrName.equalsIgnoreCase("id")) {
                annotation.append("\t@Id").append(NEWLINE).
                        append("\t@GeneratedValue(strategy=GenerationType.IDENTITY)").append(NEWLINE).
                        append("\t@Column(name=\"id_").append(typeId).append("\")").append(NEWLINE);
            } else if (attrName.equalsIgnoreCase("optlock")) {
                annotation.append("\t@Version").append(NEWLINE).
                        append("\t@Column(name=\"OPTLOCK\")").append(NEWLINE);
            } else {
                annotation.append("\t@Basic(fetch = FetchType.EAGER)").append(NEWLINE).
                        append("\t@Column(name=\"").append(attrName).append("_").append(typeId).append("\"");
                if (attrType.equalsIgnoreCase("SmallString")) {
                    annotation.append(", length=40");
                    if (attrName.equalsIgnoreCase("internalUUID"))
                        annotation.append(", unique=true)").append(NEWLINE);
                    else
                        annotation.append(")").append(NEWLINE);
                } else if (attrType.equalsIgnoreCase("BigString"))
                    annotation.append(", length=1024)").append(NEWLINE);
                else if (attrType.equalsIgnoreCase("String"))
                    annotation.append(", length=256)").append(NEWLINE);
                else if (attrType.equalsIgnoreCase("Clob")){
                    annotation.append(", length=131072)").append(NEWLINE).
                            append("\t@Lob").append(NEWLINE);
                }
                else if (attrType.equalsIgnoreCase("Date"))
                    annotation.append(")").append(NEWLINE).append("\t@Temporal(TemporalType.TIMESTAMP)").append(NEWLINE);
                else
                    annotation.append(")").append(NEWLINE);
            }
        }

        this.standardMethods_list.add(new StringBuilder(annotation).append("\tpublic ").append(attributeType).
                append(" get").append(upper_attrName).append("() {").append(NEWLINE).
                append("\t\treturn ").append(attrName).append(";").append(NEWLINE).
                append("\t}").append(NEWLINE));
        //set method
        this.standardMethods_list.add(new StringBuilder().append("\tpublic void ").
                append("set").append(upper_attrName).append("(").append(attributeType).append(" ").
                append(attrName).append(") {").append(NEWLINE).
                append("\t\tthis.").append(attrName).append("=").append(attrName).append(";").append(NEWLINE).
                append("\t}").append(NEWLINE));

    }

    private StringBuilder buildAssociationAnnotation(Class m_other_class, AssociationEnd m_asso_end, AssociationEnd other_end) {
        StringBuilder annotation = new StringBuilder();
        //we handle unidirectional association first
        if (!m_asso_end.isNavigable() && other_end.isNavigable()) {
            //one-to-one (no join table)
            if (other_end.getMultiplicityRange_upper().equals("1") &&
                    m_asso_end.getMultiplicityRange_upper().equals("1")) {
                annotation.append("\t@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},").append(NEWLINE).
                        append("\t\tfetch = FetchType.LAZY)").append(NEWLINE).
                        append("\t@JoinColumn(name=\"").append(m_other_class.getName()).append("_").append(other_end.getName()).append("_Id\",").append(NEWLINE).
                        append("\t\tunique=true, nullable=true)").append(NEWLINE);
            }
            //many-to-one  (no join table)
            if (other_end.getMultiplicityRange_upper().equals("1") &&
                    !m_asso_end.getMultiplicityRange_upper().equals("1")) {
                annotation.append("\t@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},").append(NEWLINE).
                        append("\t\tfetch = FetchType.LAZY)").append(NEWLINE).
                        append("\t@JoinColumn(name=\"").append(m_other_class.getName()).append("_").append(other_end.getName()).append("_Id\")").append(NEWLINE);
            }
            //one-to-many  (join table)
            if (!other_end.getMultiplicityRange_upper().equals("1") &&
                    m_asso_end.getMultiplicityRange_upper().equals("1")) {
                annotation.append("\t@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},").append(NEWLINE).
                        append("\t\tfetch = FetchType.LAZY").append(NEWLINE).
                        append("\t@JoinTable(").append(NEWLINE).
                        append("\t\tname=\"").append("X_").append(m_asso_end.getName()).append("_").append(other_end.getName()).append("\",").append(NEWLINE).
                        append("\t\tjoinColumns={ @JoinColumn( name=\"").append(wrapperClass.getName()).append("_").append(m_asso_end.getName()).append("_Id\") },").append(NEWLINE).
                        append("\t\tinverseJoinColumns = @JoinColumn( name=\"").append(m_other_class.getName()).append("_").append(other_end.getName()).append("_Id\")").append(NEWLINE).
                        append("\t)").append(NEWLINE);
            }
            //many-to-many (join table)
            if (!other_end.getMultiplicityRange_upper().equals("1") &&
                    !m_asso_end.getMultiplicityRange_upper().equals("1")) {
                annotation.append("\t@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},").append(NEWLINE).
                        append("\t\tfetch = FetchType.LAZY)").append(NEWLINE).
                        append("\t@JoinTable(").append(NEWLINE).
                        append("\t\tname=\"").append("X_").append(m_asso_end.getName()).append("_").append(other_end.getName()).append("\",").append(NEWLINE).
                        append("\t\tjoinColumns={ @JoinColumn( name=\"").append(wrapperClass.getName()).append("_").append(m_asso_end.getName()).append("_Id\") },").append(NEWLINE).
                        append("\t\tinverseJoinColumns = @JoinColumn( name=\"").append(m_other_class.getName()).append("_").append(other_end.getName()).append("_Id\")").append(NEWLINE).
                        append("\t)").append(NEWLINE);
            }
        }
        //bidirectional associations
        if (m_asso_end.isNavigable() && other_end.isNavigable()) {
            //many-to-one  (no join table)
            if (other_end.getMultiplicityRange_upper().equals("1") &&
                    !m_asso_end.getMultiplicityRange_upper().equals("1")) {
                annotation.append("\t@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},").append(NEWLINE).
                        append("\t\tfetch = FetchType.LAZY)").append(NEWLINE).
                        append("\t@JoinColumn(name=\"").append(m_other_class.getName()).append("_").append(other_end.getName()).append("_Id\")").append(NEWLINE);
            }
            //one-to-many  (no join table)
            if (!other_end.getMultiplicityRange_upper().equals("1") &&
                    m_asso_end.getMultiplicityRange_upper().equals("1")) {
                //special handling for classifier-feature association
                if (other_end.getName().equalsIgnoreCase("feature"))
                    annotation.append("\t@OneToMany(cascade = {CascadeType.ALL},").append(NEWLINE).
                            append("\t\tfetch = FetchType.LAZY,").append(NEWLINE).
                            append("\t\tmappedBy=").append("\"").append(m_asso_end.getName()).append("\")").append(NEWLINE);
                else
                    annotation.append("\t@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},").append(NEWLINE).
                            append("\t\tfetch = FetchType.LAZY,").append(NEWLINE).
                            append("\t\tmappedBy=").append("\"").append(m_asso_end.getName()).append("\")").append(NEWLINE);
            }
            //one-to-one (no joinn table)
            if (other_end.getMultiplicityRange_upper().equals("1") &&
                    m_asso_end.getMultiplicityRange_upper().equals("1")) {
                if (oneToOneSet.contains(other_end.getAssociationEndID())) {
                    annotation.append("\t@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},").append(NEWLINE).
                            append("\t\tfetch = FetchType.LAZY)").append(NEWLINE).
                            append("\t@JoinColumn(name=\"").append(m_other_class.getName()).append("_").append(other_end.getName()).append("_Id").
                            append("\", unique=true, nullable=true)").append(NEWLINE);
                } else {
                    annotation.append("\t@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},").append(NEWLINE).
                            append("\t\tfetch = FetchType.LAZY,").append(NEWLINE).
                            append("\t\tmappedBy = \"").append(m_asso_end.getName()).append("\")").append(NEWLINE);
                }
                oneToOneSet.add(m_asso_end.getAssociationEndID());
            }
            //many-to-many  (join table)
            if (!other_end.getMultiplicityRange_upper().equals("1") &&
                    !m_asso_end.getMultiplicityRange_upper().equals("1")) {
                annotation.append("\t@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},").append(NEWLINE).
                        append("\t\tfetch = FetchType.LAZY");
                if (manyToManySet.contains(other_end.getAssociationEndID())) {
                    annotation.append(",").append(NEWLINE).
                            append("\t\tmappedBy = \"").append(m_asso_end.getName()).append("\")").append(NEWLINE);
                } else {
                    annotation.append(")").append(NEWLINE).
                            append("\t@JoinTable(").append(NEWLINE).
                            append("\t\tname=\"").append("X_").append(m_asso_end.getName()).append("_").append(other_end.getName()).append("\",").append(NEWLINE).
                            append("\t\tjoinColumns={ @JoinColumn( name=\"").append(wrapperClass.getName()).append("_").append(m_asso_end.getName()).append("_Id\") },").append(NEWLINE).
                            append("\t\tinverseJoinColumns = @JoinColumn( name=\"").append(m_other_class.getName()).append("_").append(other_end.getName()).append("_Id\")").append(NEWLINE).
                            append("\t)").append(NEWLINE);
                }
                manyToManySet.add(m_asso_end.getAssociationEndID());

            }
        }

        return annotation;
    }

    protected void buildAssociationMethods(Class otherClass, AssociationEnd thisEnd, AssociationEnd otherEnd) {
        String attrType = otherClass.getName();
        StringBuilder upper_attrName = new StringBuilder(otherEnd.getName());
        upper_attrName.setCharAt(0, Character.toUpperCase(upper_attrName.charAt(0)));
        StringBuilder annotation = buildAssociationAnnotation(otherClass, thisEnd, otherEnd);

        StringBuilder instanceName = new StringBuilder("i_").append(attrType);
        String multiplicityRange_upper = thisEnd.getMultiplicityRange_upper();
        StringBuilder m_thisEndName = new StringBuilder(thisEnd.getName());
        m_thisEndName.setCharAt(0, Character.toUpperCase(m_thisEndName.charAt(0)));
        if (otherEnd.isNavigable() && !otherEnd.getMultiplicityRange_upper().equals("1")) { //isList
            //get method. For simplicity, for boolean datatype, we also use get<PropertyName> method.
            this.standardMethods_list.add(new StringBuilder().append(annotation).
                    append("\tpublic Set<").append(attrType).append("> ").
                    append(" get").append(upper_attrName).append("() {").append(NEWLINE).
                    append("\t\treturn ").append(otherEnd.getName()).append(";").append(NEWLINE).
                    append("\t}").append(NEWLINE));
            //set method -- when HB calls session.persist, it will call each set method again for each attribute.
            this.standardMethods_list.add(new StringBuilder().append("\tprotected void ").
                    append("set").append(upper_attrName).append("(").append("Set<").append(attrType).append("> ").
                    append(otherEnd.getName()).append(") {").append(NEWLINE).
                    append("\t\tthis.").append(otherEnd.getName()).append(" = ").append(otherEnd.getName()).append(";").append(NEWLINE).append("\t}").append(NEWLINE));
            //internal set method
            this.standardMethods_list.add(new StringBuilder().append("\tpublic void ").
                    append("internalSet").append(upper_attrName).append("(").append("Set<").append(attrType).append("> ").
                    append(otherEnd.getName()).append(") {").append(NEWLINE).
                    append("\t\tthis.").append(otherEnd.getName()).append(" = ").append(otherEnd.getName()).append(";").append(NEWLINE).append("\t}").append(NEWLINE));
        } else if (otherEnd.isNavigable() && otherEnd.getMultiplicityRange_upper().equals("1")) { // not List
            //get method
            this.standardMethods_list.add(new StringBuilder().append(annotation).
                    append("\tpublic ").append(attrType).
                    append(" get").append(upper_attrName).append("() {").append(NEWLINE).
                    append("\t\treturn ").append(otherEnd.getName()).append(";").append(NEWLINE).
                    append("\t}").append(NEWLINE));
            //set method
            StringBuilder m_setMethod = new StringBuilder().append("\tpublic void set").
                    append(upper_attrName).append("(").append(attrType).append(" ").append(instanceName).append("){").append(NEWLINE);

            if (!thisEnd.isNavigable()) {
                m_setMethod.append("\t\tif (this.get").append(upper_attrName).append("() != null && ").append(instanceName).
                        append(".equals(this.get").append(upper_attrName).append("()))").append(NEWLINE).append("\t\t\t\treturn;").
                        append(NEWLINE).append(NEWLINE);
                m_setMethod.append("\t\tthis.").append(otherEnd.getName()).append("=").append(instanceName).append(";").
                        append(NEWLINE);
            } else {
                if (multiplicityRange_upper.equals("1")) { //one-to-one
                    m_setMethod.append("\t\tif (").append(instanceName).append(" == null) {").append(NEWLINE).
                            append("\t\t\tif (this.get").append(upper_attrName).append("() != null){").append(NEWLINE).
                            append("\t\t\t\tif (this.get").append(upper_attrName).append("().get").append(m_thisEndName).append("() != null)").append(NEWLINE).
                            append("\t\t\t\t\tthis.get").append(upper_attrName).append("().internalSet").append(m_thisEndName).append("(null);").append(NEWLINE).
                            append("\t\t\t} else{").append(NEWLINE).append("\t\t\t\treturn;").append(NEWLINE).append("\t\t\t}").
                            append(NEWLINE).append(NEWLINE);
                    m_setMethod.append("\t\t\tthis.").append(otherEnd.getName()).append("=").append(instanceName).append(";").
                            append(NEWLINE).append("\t\t}");

                    m_setMethod.append("else {").append(NEWLINE).
                            append("\t\t\tif (").append(instanceName).append(".get").append(m_thisEndName).
                            append("() != null )").append(NEWLINE).
                            append("\t\t\t\t").append(instanceName).append(".get").append(m_thisEndName).append("().internalSet").
                            append(upper_attrName).append("(null);").append(NEWLINE).append(NEWLINE);
                    m_setMethod.append("\t\t\t").append(instanceName).append(".internalSet").append(m_thisEndName).append("(this);").
                            append(NEWLINE).append(NEWLINE);
                    m_setMethod.append("\t\t\tif (this.get").append(upper_attrName).append("() != null )").append(NEWLINE).
                            append("\t\t\t\tthis.get").append(upper_attrName).append("().internalSet").append(m_thisEndName).append("(null);").
                            append(NEWLINE).append(NEWLINE);
                    m_setMethod.append("\t\t\tthis.").append(otherEnd.getName()).append("=").append(instanceName).append(";").
                            append(NEWLINE).append("\t\t}").append(NEWLINE);
                } else {//many-to-one
                    m_setMethod.append("\t\tif (").append(instanceName).append(" == null) {").append(NEWLINE).
                            append("\t\t\tif (this.get").append(upper_attrName).append("() != null)").append(NEWLINE).
                            append("\t\t\t\tthis.get").append(upper_attrName).append("().get").append(m_thisEndName).
                            append("().remove(this);").append(NEWLINE).
                            append("\t\t\telse").append(NEWLINE).append("\t\t\t\treturn;").append(NEWLINE).append(NEWLINE);
                    m_setMethod.append("\t\t\tthis.").append(otherEnd.getName()).append("=").append(instanceName).append(";").
                            append(NEWLINE).append("\t\t}");

                    m_setMethod.append("else {").append(NEWLINE).
                            append("\t\t\tif (this.get").append(upper_attrName).append("() != null && ").append(instanceName).
                            append(".equals(this.get").append(upper_attrName).append("()))").append(NEWLINE).append("\t\t\t\treturn;").
                            append(NEWLINE).append(NEWLINE).
                            append("\t\t\tif (this.get").append(upper_attrName).append("() != null)").append(NEWLINE).
                            append("\t\t\t\tthis.get").append(upper_attrName).append("().get").append(m_thisEndName).
                            append("().remove(this);").append(NEWLINE).append(NEWLINE);

                    m_setMethod.append("\t\t\tthis.").append(otherEnd.getName()).append("=").append(instanceName).append(";").
                            append(NEWLINE).append(NEWLINE);

                    m_setMethod.append("\t\t\tif (!(").append(instanceName).append(".get").append(m_thisEndName).append("() instanceof org.hibernate.collection.PersistentCollection))").append(NEWLINE);
                    m_setMethod.append("\t\t\t\t").append(instanceName).append(".get").append(m_thisEndName).append("().add(this);").
                            append(NEWLINE).append("\t\t}").append(NEWLINE);
                }
            }
            m_setMethod.append("\t}").append(NEWLINE);
            this.standardMethods_list.add(m_setMethod);

            //internal set method
            StringBuilder m_internalSetMethod = new StringBuilder().append("\tpublic void internalSet").
                    append(upper_attrName).append("(").append(attrType).append(" ").append(instanceName).append("){").append(NEWLINE);
            m_internalSetMethod.append("\t\tthis.").append(otherEnd.getName()).append("=").append(instanceName).append(";").
                    append(NEWLINE);
            m_internalSetMethod.append("\t}").append(NEWLINE);
            this.standardMethods_list.add(m_internalSetMethod);
        }
    }

    private void buildOptionalMethods(String attrType, AssociationEnd thisEnd, AssociationEnd otherEnd) {
        StringBuilder upper_attrName = new StringBuilder(otherEnd.getName());
        upper_attrName.setCharAt(0, Character.toUpperCase(upper_attrName.charAt(0)));

        StringBuilder instanceName = new StringBuilder("i_").append(attrType);
        String multiplicityRange_upper = thisEnd.getMultiplicityRange_upper();
        StringBuilder m_thisEndName = new StringBuilder(thisEnd.getName());
        m_thisEndName.setCharAt(0, Character.toUpperCase(m_thisEndName.charAt(0)));
        if (otherEnd.isNavigable() && !otherEnd.getMultiplicityRange_upper().equals("1")) { //isList
            //add method
            StringBuilder m_addMethod = new StringBuilder().append("\tpublic void ").
                    append("add").append(upper_attrName).append("(").append(attrType).append(" ").
                    append(instanceName).append(") {").append(NEWLINE);
            m_addMethod.append("\t\tif (").append(instanceName).append(" == null)").append(NEWLINE).
                    append("\t\t\t\tthrow new IllegalArgumentException(\"Null Object Parameter!\");").append(NEWLINE).append(NEWLINE);

            if (thisEnd.isNavigable()) {
                if (multiplicityRange_upper.equals("1")) {
                    m_addMethod.append("\t\tif (").append(instanceName).append(".get").append(m_thisEndName).append("()!=null)").append(NEWLINE).
                            append("\t\t\t\t").append(instanceName).append(".get").append(m_thisEndName).append("().get").
                            append(upper_attrName).append("().remove(").append(instanceName).append(");").append(NEWLINE).append(NEWLINE);
                    m_addMethod.append("\t\tthis.").append(otherEnd.getName()).append(".add(").append(instanceName).append(");").
                            append(NEWLINE).append(NEWLINE);

                    m_addMethod.append("\t\t").append(instanceName).append(".set").append(m_thisEndName).append("(this);").append(NEWLINE).
                            append("\t}").append(NEWLINE);
                } else {
                    m_addMethod.append("\t\tthis.").append(otherEnd.getName()).append(".add(").append(instanceName).append(");").
                            append(NEWLINE).append(NEWLINE);

                    m_addMethod.append("\t\tif (!").append(instanceName).append(".contains").append(m_thisEndName).append("(this))").append(NEWLINE).
                            append("\t\t\t\t").append(instanceName).append(".add").append(m_thisEndName).
                            append("(this);").append(NEWLINE).append(NEWLINE).append("\t}").append(NEWLINE);
                }
            }

            this.standardMethods_list.add(m_addMethod);

            //contain method
            StringBuilder m_containMethod = new StringBuilder().append("\tpublic boolean contains").
                    append(upper_attrName).append("(").append(attrType).append(" ").append(instanceName).append(") {").append(NEWLINE);
            m_containMethod.append("\t\treturn this.").append(otherEnd.getName()).append(".contains(").append(instanceName).append(");").
                    append(NEWLINE).append("\t}").append(NEWLINE);

            this.standardMethods_list.add(m_containMethod);

            //remove method
            StringBuilder m_removeMethod = new StringBuilder().append("\tpublic void remove").
                    append(upper_attrName).append("(").append(attrType).append(" ").
                    append(instanceName).append(") {").append(NEWLINE);
            m_removeMethod.append("\t\tif (").append(instanceName).append(" == null )").append(NEWLINE).
                    append("\t\t\t\tthrow new IllegalArgumentException(\"Null Object Parameter!\");").append(NEWLINE).append(NEWLINE);
            if (thisEnd.isNavigable()) {
                if (multiplicityRange_upper.equals("1")) {
                    m_removeMethod.append("\t\tif (this.").append(otherEnd.getName()).append(".remove(").append(instanceName).append("))").append(NEWLINE);
                    m_removeMethod.append("\t\t\t\t").append(instanceName).append(".internalSet").append(m_thisEndName).append("(null);").append(NEWLINE).
                            append("\t}").append(NEWLINE);
                } else {
                    m_removeMethod.append("\t\tif (").append(instanceName).append(".contains").append(m_thisEndName).append("(this))").append(NEWLINE);
                    m_removeMethod.append("\t\t\t\t").append(instanceName).append(".get").append(m_thisEndName).
                            append("().remove(this);").append(NEWLINE).append(NEWLINE);
                    m_removeMethod.append("\t\tthis.").append(otherEnd.getName()).append(".remove(").append(instanceName).append(");").append(NEWLINE).
                            append("\t}").append(NEWLINE);
                }
            } else {
                m_removeMethod.append("\t\tthis.").append(otherEnd.getName()).append(".remove(").append(instanceName).append(");").append(NEWLINE).
                        append("\t}").append(NEWLINE);
            }

            this.standardMethods_list.add(m_removeMethod);
        } else if (otherEnd.isNavigable() && otherEnd.getMultiplicityRange_upper().equals("1")) {
            //change method
            StringBuilder m_changeMethod = new StringBuilder().append("\tpublic void change").
                    append(upper_attrName).append("(").append(attrType).append(" ").append(instanceName).append("){").append(NEWLINE);
            m_changeMethod.append("\t\tset").append(upper_attrName).append("(").append(instanceName).append(");").append(NEWLINE);
            m_changeMethod.append("\t}").append(NEWLINE);

            this.standardMethods_list.add(m_changeMethod);
        }
    }

}
