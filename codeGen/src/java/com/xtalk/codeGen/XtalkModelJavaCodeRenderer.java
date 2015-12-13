package com.xtalk.codeGen;

import com.xtalk.codeGen.objectType.Class;

import java.util.ArrayList;
import java.util.List;


public class XtalkModelJavaCodeRenderer {
    private List<XtalkModelJavaClass> javaClass_list = new ArrayList<XtalkModelJavaClass>();
    private static XtalkModelJavaCodeRenderer javaCodeRenderer = new XtalkModelJavaCodeRenderer();

    private XtalkModelJavaCodeRenderer() {
    }

    public static XtalkModelJavaCodeRenderer getInstance() {
        return javaCodeRenderer;
    }

    public XtalkModelJavaClass createRendererJavaClass(Class i_class) {
        XtalkModelJavaClass i_javaClass = new XtalkModelJavaClass(i_class);

        i_javaClass.setupClassPackage();

        i_javaClass.setupClassHeader();
        i_javaClass.setupClassFooter();
        i_javaClass.setupConstructor_block();
        i_javaClass.setupCustomizedImport_block();
        i_javaClass.setupCustomizedMethod_block();

        i_javaClass.setupClassComment_block();
        i_javaClass.setupAttribute_block();
        i_javaClass.setupAssociation_block();
        i_javaClass.setupStandardImport_block();
        i_javaClass.setupStandardMethods_block();

        return i_javaClass;
    }


    public List<XtalkModelJavaClass> getJavaClass_list() {
        return javaClass_list;
    }

    public String assembleJavaCode(XtalkModelJavaClass i_javaClass) {
        StringBuilder temp_classBuffer = new StringBuilder();
        temp_classBuffer.append(i_javaClass.getClassPackage()).
                append(XtalkModelJavaClass.NEWLINE).append(XtalkModelJavaClass.NEWLINE).
                append(i_javaClass.getStandardImport_block()==null? "":i_javaClass.getStandardImport_block()).
                append(XtalkModelJavaClass.NEWLINE).append(XtalkModelJavaClass.NEWLINE).
                append(i_javaClass.getCustomizedImport_block()).
                append(XtalkModelJavaClass.NEWLINE).append(XtalkModelJavaClass.NEWLINE).
                append(i_javaClass.getClassComment_block()).
                append(XtalkModelJavaClass.NEWLINE).append(XtalkModelJavaClass.NEWLINE).
                append(i_javaClass.getClassHeader()).
                append(XtalkModelJavaClass.NEWLINE).append(XtalkModelJavaClass.NEWLINE).
                append(i_javaClass.getAttribute_block()).
                append(XtalkModelJavaClass.NEWLINE).append(XtalkModelJavaClass.NEWLINE).
                append(i_javaClass.getAssociation_block()).
                append(XtalkModelJavaClass.NEWLINE).append(XtalkModelJavaClass.NEWLINE).
                append(i_javaClass.getConstructor_block()).
                append(XtalkModelJavaClass.NEWLINE).append(XtalkModelJavaClass.NEWLINE).
                append(i_javaClass.getStandardMethods_block()).
                append(XtalkModelJavaClass.NEWLINE).append(XtalkModelJavaClass.NEWLINE).
                append(i_javaClass.getCustomizedMethod_block()).
                append(XtalkModelJavaClass.NEWLINE).append(XtalkModelJavaClass.NEWLINE).
                append(i_javaClass.getClassFooter());


        return temp_classBuffer.toString();
    }
}
