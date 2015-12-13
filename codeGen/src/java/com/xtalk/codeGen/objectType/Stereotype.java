package com.xtalk.codeGen.objectType;

/**
 * Created by IntelliJ IDEA.
 * User: mzfan
 * Date: Feb 17, 2007
 * Time: 9:35:54 AM
 */
public class Stereotype {
    private String stereotypeID;
    private String name;
    private String visibility;
    private boolean isSpecification;
    private boolean isRoot;
    private boolean isLeaf;
    private boolean isAbstract;
    private String icon;
    private String baseClass;
    private String extendedElements;
    private Package parentPackage;

    public Stereotype() {
    }

    public Stereotype(String stereotypeID) {
        this.stereotypeID = stereotypeID;
    }

    public String getStereotypeID() {
        return stereotypeID;
    }

    public void setStereotypeID(String stereotypeID) {
        this.stereotypeID = stereotypeID;
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

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBaseClass() {
        return baseClass;
    }

    public void setBaseClass(String baseClass) {
        this.baseClass = baseClass;
    }

    public String getExtendedElements() {
        return extendedElements;
    }

    public void setExtendedElements(String extendedElements) {
        this.extendedElements = extendedElements;
    }

    public Package getParentPackage() {
        return parentPackage;
    }

    public void setParentPackage(Package parentPackage) {
        this.parentPackage = parentPackage;
    }
}
