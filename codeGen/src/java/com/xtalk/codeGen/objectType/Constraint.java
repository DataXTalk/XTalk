package com.xtalk.codeGen.objectType;

/**
 * Created by IntelliJ IDEA.
 * User: mzfan
 * Date: Feb 16, 2007
 * Time: 11:14:08 PM
 */
public class Constraint {
    private String constraintName;
    private String constraintID;
    private String visibility;
    private boolean isSpecification;
    private String constrainedElement;
    private String expressionBody;
    private Package parentPackage;

    public Constraint() {
    }

    public Constraint(String constraintID) {
        this.constraintID = constraintID;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    public String getConstraintID() {
        return constraintID;
    }

    public void setConstraintID(String constraintID) {
        this.constraintID = constraintID;
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

    public String getConstrainedElement() {
        return constrainedElement;
    }

    public void setConstrainedElement(String constrainedElement) {
        this.constrainedElement = constrainedElement;
    }

    public String getExpressionBody() {
        return expressionBody;
    }

    public void setExpressionBody(String expressionBody) {
        this.expressionBody = expressionBody;
    }

    public Package getParentPackage() {
        return parentPackage;
    }

    public void setParentPackage(Package parentPackage) {
        this.parentPackage = parentPackage;
    }
}
