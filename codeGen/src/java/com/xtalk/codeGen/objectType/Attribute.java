package com.xtalk.codeGen.objectType;

/**
 * Created by IntelliJ IDEA.
 * User: mzfan
 * Date: Feb 16, 2007
 * Time: 11:37:59 PM
 */
public class Attribute {
    private String attributeID;
    private String attributeName;
    private String visibility;
    private boolean isSpecification;
    private String ownerScope;
    private String changeability;
    private String targetScope;
    private String type;
    private String multiplicityRange_lower;
    private String multiplicityRange_upper;
    private String initialValue;

    public Attribute() {
    }

    public Attribute(String attributeID) {
        this.attributeID = attributeID;
    }

    public String getAttributeID() {
        return attributeID;
    }

    public void setAttributeID(String attributeID) {
        this.attributeID = attributeID;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
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

    public String getOwnerScope() {
        return ownerScope;
    }

    public void setOwnerScope(String ownerScope) {
        this.ownerScope = ownerScope;
    }

    public String getChangeability() {
        return changeability;
    }

    public void setChangeability(String changeability) {
        this.changeability = changeability;
    }

    public String getTargetScope() {
        return targetScope;
    }

    public void setTargetScope(String targetScope) {
        this.targetScope = targetScope;
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

    public String getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }
}
