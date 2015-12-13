package com.xtalk.codeGen.objectType;

/**
 * Created by IntelliJ IDEA.
 * User: mzfan
 * Date: Feb 16, 2007
 * Time: 10:57:30 PM
 */
public class AssociationEnd {
    private String associationEndID;
    private String name;
    private boolean isSpecification;
    private boolean isNavigable;
    private String ordering;
    private String aggregation;
    private String targetScope;
    private String changeability;
    private String type;
    private String constraint;
    private String multiplicityRange_lower;
    private String multiplicityRange_upper;

    private Association ownerAssociation;

    public AssociationEnd() {
    }

    public AssociationEnd(String associationEndID) {
        this.associationEndID = associationEndID;
    }

    public String getAssociationEndID() {
        return associationEndID;
    }

    public void setAssociationEndID(String associationEndID) {
        this.associationEndID = associationEndID;
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

    public boolean isNavigable() {
        return isNavigable;
    }

    public void setNavigable(boolean navigable) {
        isNavigable = navigable;
    }

    public String getOrdering() {
        return ordering;
    }

    public void setOrdering(String ordering) {
        this.ordering = ordering;
    }

    public String getAggregation() {
        return aggregation;
    }

    public void setAggregation(String aggregation) {
        this.aggregation = aggregation;
    }

    public String getTargetScope() {
        return targetScope;
    }

    public void setTargetScope(String targetScope) {
        this.targetScope = targetScope;
    }

    public String getChangeability() {
        return changeability;
    }

    public void setChangeability(String changeability) {
        this.changeability = changeability;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMultiplicityRange_lower() {
        return multiplicityRange_lower;
    }

    public void setMultiplicityRange_lower(String multiplicityRange_lower) {
        this.multiplicityRange_lower = multiplicityRange_lower;
    }

    public String getMultiplicityRange_upper() {
        return multiplicityRange_upper;
    }

    public void setMultiplicityRange_upper(String multiplicityRange_upper) {
        this.multiplicityRange_upper = multiplicityRange_upper;
    }

    public String getConstraint() {
        return constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public Association getOwnerAssociation() {
        return ownerAssociation;
    }

    public void setOwnerAssociation(Association ownerAssociation) {
        this.ownerAssociation = ownerAssociation;
    }

}
