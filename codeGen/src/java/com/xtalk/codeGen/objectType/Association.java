package com.xtalk.codeGen.objectType;

import java.util.List;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: mzfan
 * Date: Feb 16, 2007
 * Time: 10:45:27 PM
 */
public class Association {
    private String associationID;
    private String visibility;
    private String name;
    private boolean isSpecification;
    private boolean isRoot;
    private boolean isLeaf;
    private boolean isAbstract;
    private boolean isDerived;
    private Package containingPackage;
    private List<AssociationEnd> associationEndList;

    public Association() {
        associationEndList = new Vector<AssociationEnd>();
    }

    public Association(String associationID) {
        this.associationID = associationID;
    }

    public String getAssociationID() {
        return associationID;
    }

    public void setAssociationID(String associationID) {
        this.associationID = associationID;
    }

    public AssociationEnd getOtherAssociationEnd(AssociationEnd thisEnd){
        for (AssociationEnd m_asso_end : associationEndList){
             if (!m_asso_end.getAssociationEndID().equals(thisEnd.getAssociationEndID())){
                 return m_asso_end;
             }
        }
        return null;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Package getContainingPackage() {
        return containingPackage;
    }

    public void setContainingPackage(Package containingPackage) {
        this.containingPackage = containingPackage;
    }

    public List<AssociationEnd> getAssociationEndList() {
        return associationEndList;
    }

    public void addAssociationEnd(AssociationEnd associationEnd) {
        this.associationEndList.add(associationEnd);
    }

    public boolean isDerived() {
        return isDerived;
    }

    public void setDerived(boolean derived) {
        isDerived = derived;
    }
}
