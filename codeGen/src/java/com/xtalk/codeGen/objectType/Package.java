package com.xtalk.codeGen.objectType;

/**
 * Created by IntelliJ IDEA.
 * User: mzfan
 * Date: Feb 16, 2007
 * Time: 10:25:04 PM
 */
public class Package {
    private String packageID;
    private String name;
    private String visibility;
    private boolean isSpecification;
    private boolean isRoot;
    private boolean isLeaf;
    private boolean isAbstract;
    private String nameSpace;
    private String supplierDependency;
    private String nameSpacePath;

    private boolean isFinishedProcessing = false;

    public Package() {
    }

    public Package(String packageID) {
        this.packageID = packageID;
    }

    public String getPackageID() {
        return packageID;
    }

    public void setPackageID(String packageID) {
        this.packageID = packageID;
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

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getSupplierDependency() {
        return supplierDependency;
    }

    public void setSupplierDependency(String supplierDependency) {
        this.supplierDependency = supplierDependency;
    }

    public String getNameSpacePath() {
        return nameSpacePath;
    }

    public void setNameSpacePath(String nameSpacePath) {
        this.nameSpacePath = nameSpacePath;
    }

    public boolean isFinishedProcessing() {
        return isFinishedProcessing;
    }

    public void setFinishedProcessing(boolean finishedProcessing) {
        isFinishedProcessing = finishedProcessing;
    }
}
