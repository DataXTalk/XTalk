package com.xtalk.codeGen.objectType;

/**
 * Created by IntelliJ IDEA.
 * User: mzfan
 * Date: Feb 17, 2007
 * Time: 9:44:34 AM
 */
public class TaggedValue {
    private String taggedValueID;
    private String tag;
    private String value;
    private String modelElement;

    public TaggedValue() {
    }

    public TaggedValue(String taggedValueID) {
        this.taggedValueID = taggedValueID;
    }

    public String getTaggedValueID() {
        return taggedValueID;
    }

    public void setTaggedValueID(String taggedValueID) {
        this.taggedValueID = taggedValueID;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getModelElement() {
        return modelElement;
    }

    public void setModelElement(String modelElement) {
        this.modelElement = modelElement;
    }
}
