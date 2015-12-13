package com.xtalk.codeGen.objectType;

import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: mzfan
 * Date: Feb 16, 2007
 * Time: 11:23:12 PM
 */
public class Class {
    private String classID;
    private String name;
    private String visibility;
    private boolean isSpecification;
    private boolean isLeaf;
    private boolean isRoot;
    private boolean isAbstract;
    private boolean isActive;
    private String nameSpace;
    private String specialization;
    private Class parentClass;
    private String parentClassID;
    private List<Attribute> attributeList;
    private boolean isEnumType = false;
    private boolean isDataType = false;

    private Package ownerPackage;

    private String customizedImports;
    private String customizedMethods;

    public Class() {
        attributeList = new Vector<Attribute>();
    }

    public Class(String classID) {
        this.classID = classID;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public boolean isSpecification() {
        return isSpecification;
    }

    public void setSpecification(boolean specification) {
        isSpecification = specification;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Class getParentClass() {
        return parentClass;
    }

    public void setParentClass(Class parentClass) {
        this.parentClass = parentClass;
    }

    public List<Attribute> getAttributeList() {
        return attributeList;
    }

    public void addAttribute(Attribute attribute) {
        this.attributeList.add(attribute);
    }

    public String getCustomizedImports() {
        return customizedImports;
    }

    public void setCustomizedImports(String customizedImports) {
        this.customizedImports = customizedImports;
    }

    public String getCustomizedMethods() {
        return customizedMethods;
    }

    public void setCustomizedMethods(String customizedMethods) {
        this.customizedMethods = customizedMethods;
    }

    public Package getOwnerPackage() {
        return ownerPackage;
    }

    public void setOwnerPackage(Package ownerPackage) {
        this.ownerPackage = ownerPackage;
    }

    public String getParentClassID() {
        return parentClassID;
    }

    public void setParentClassID(String parentClassID) {
        this.parentClassID = parentClassID;
    }

    public boolean isEnumType() {
        return isEnumType;
    }

    public void setEnumType(boolean enumType) {
        isEnumType = enumType;
    }

    public boolean isDataType() {
        return isDataType;
    }

    public void setDataType(boolean dataType) {
        isDataType = dataType;
    }
}
