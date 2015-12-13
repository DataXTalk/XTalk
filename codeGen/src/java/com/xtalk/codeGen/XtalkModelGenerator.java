package com.xtalk.codeGen;

import static com.xtalk.codeGen.XtalkModelJavaClass.NEWLINE;
import com.xtalk.codeGen.objectType.Association;
import com.xtalk.codeGen.objectType.AssociationEnd;
import com.xtalk.codeGen.objectType.Attribute;
import com.xtalk.codeGen.objectType.Class;
import com.xtalk.codeGen.objectType.Constraint;
import com.xtalk.codeGen.objectType.Package;
import com.xtalk.codeGen.objectType.Stereotype;
import com.xtalk.codeGen.objectType.TaggedValue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class XtalkModelGenerator {
    static Deque<Package> PACKAGELIST = new ArrayDeque<Package>();
    static Vector<Association> ASSOCIATIONLIST = new Vector<Association>();
    static Vector<AssociationEnd> ASSOCIATIONENDLIST = new Vector<AssociationEnd>();
    static Vector<Attribute> ATTRIBUTELIST = new Vector<Attribute>();
    static Vector<Class> CLASSLIST = new Vector<Class>();
    static Vector<Constraint> CONSTRAINTLIST = new Vector<Constraint>();
    static Vector<Stereotype> STEREOTYPELIST = new Vector<Stereotype>();
    static Vector<TaggedValue> TAGGEDVALUELIST = new Vector<TaggedValue>();
    //static Vector<Datatype> DATATYPELIST = new Vector<Datatype>();

    static List<String> FULLCLASSNAMELIST = new ArrayList<String>();
    static List<String> MAPPINGFILENAMELIST = new ArrayList<String>();

    static Set<String> manyToManySet = new HashSet<String>();
    static Set<String> oneToOneSet = new HashSet<String>();

    private enum CODEGEN_PASSES {
        FIRST_PASS, SECOND_PASS
    }

    private static final String FILESEPARATOR = System.getProperty("file.separator");

    static final String PERSISTENTDIRECTORY = "persistence" + FILESEPARATOR + "src" + FILESEPARATOR + "java";
    static final String CODEGENDIRECTORY = "codeGen" + FILESEPARATOR + "src" + FILESEPARATOR + "java";
    static final String CONFIGDIRECTORY = "etc" + FILESEPARATOR + "META-INF";

    private void createPackage() throws IOException {
        for (Package m_package : PACKAGELIST) {
            new File(PERSISTENTDIRECTORY, m_package.getNameSpacePath()).mkdirs();
            StringBuilder pkg_doc = new StringBuilder(XtalkModelJavaClass.getDocument(m_package.getPackageID()));

            File pkg_doc_file = new File(PERSISTENTDIRECTORY + FILESEPARATOR + m_package.getNameSpacePath(), "package.html");
            pkg_doc_file.createNewFile();
            PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(pkg_doc_file)), true);
            printer.println(pkg_doc.toString());
            printer.close();
        }
    }

    private void createClass(CODEGEN_PASSES i_passes) throws IOException {
        for (Class m_class : CLASSLIST) {
            if (!m_class.isDataType()) {
                File m_sourcefile = new File(PERSISTENTDIRECTORY + FILESEPARATOR + m_class.getOwnerPackage().getNameSpacePath(),
                        m_class.getName() + ".java");
                if (!m_sourcefile.createNewFile() && i_passes == CODEGEN_PASSES.FIRST_PASS) {
                    extractModifiedCodes(m_class, m_sourcefile);
                }
                if (i_passes == CODEGEN_PASSES.SECOND_PASS) {
                    populateClassContent(m_class, m_sourcefile);
                }
            }
        }
    }

    private void extractModifiedCodes(Class i_class, File i_file) throws IOException {
        //open i_file, extract out customized imports
        StringBuilder m_customizedImports = new StringBuilder();
        StringBuilder m_customizedMethods = new StringBuilder();

        BufferedReader in = new BufferedReader(new FileReader(i_file));
        String m_line = in.readLine();

        while (m_line != null) {
            if (m_line.contains("***Begin Imports***")) {
                String next_line = in.readLine();
                while (!next_line.contains("***End Imports***")) {
                    m_customizedImports.append(next_line).append(NEWLINE);
                    next_line = in.readLine();
                }
            }
            if (m_line.contains("***Begin Methods***")) {
                String next_line = in.readLine();
                while (!next_line.contains("***End Methods***")) {
                    m_customizedMethods.append(next_line).append(NEWLINE);
                    next_line = in.readLine();
                }
            }
            m_line = in.readLine();
        }

        i_class.setCustomizedImports(m_customizedImports.toString());

        //extract out customized methods
        i_class.setCustomizedMethods(m_customizedMethods.toString());

        //close the file
        in.close();
    }

    private void populateClassContent(Class i_class, File i_file) throws IOException {
        PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(i_file)), true);

        XtalkModelJavaCodeRenderer m_javaCodeRenderer = XtalkModelJavaCodeRenderer.getInstance();
        XtalkModelJavaClass m_javaClass = m_javaCodeRenderer.createRendererJavaClass(i_class);

        printer.println(m_javaCodeRenderer.assembleJavaCode(m_javaClass));

        printer.close();

    }
    

    private void deleteAllCodeGen() {
        deleteDir(new File(PERSISTENTDIRECTORY, PACKAGELIST.getFirst().getName()));
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            for (String child : dir.list()) {
                if (!deleteDir(new File(dir, child))) {
                    return false;
                }
            }
        }
        return dir.delete();
    }


    /**
     * This method does two passes to handle file creation:
     * First pass, try to create a new file, if the file exists, try to get modified code;
     * Second pass, erase everything, dump out the brand new file and directories, plus saved modified code.
     *
     * @throws IOException - if file not exist
     */
    public void createPackagesAndClasses() throws Exception {
        createPackage();

        createClass(CODEGEN_PASSES.FIRST_PASS);

        deleteAllCodeGen();

        createPackage();

        createClass(CODEGEN_PASSES.SECOND_PASS);

        createClassIDClass();

        //createHibernateMappingFiles();

    }

    private void createClassIDClass() throws Exception {
        String m_classIdClassPath = "com" + FILESEPARATOR + "xtalk" + FILESEPARATOR + "codeGen" + FILESEPARATOR + "util";
        File m_classIDfile = new File(CODEGENDIRECTORY + FILESEPARATOR + m_classIdClassPath, "GeneratedClassID.java");
        m_classIDfile.createNewFile();

        FULLCLASSNAMELIST.clear();
        for (Class m_class : CLASSLIST) {
            if (!m_class.isEnumType() && !m_class.isDataType()) {
                FULLCLASSNAMELIST.add(m_class.getOwnerPackage().getNameSpacePath().replace(System.getProperty("file.separator").charAt(0), '.')
                        + "." + m_class.getName());
            }
        }

        Collections.sort(FULLCLASSNAMELIST);

        PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(m_classIDfile)), true);

        StringBuilder m_classIdClass = new StringBuilder().append("package ").append(m_classIdClassPath.
                replace(System.getProperty("file.separator").charAt(0), '.')).append(";").
                append(NEWLINE).append(NEWLINE);
        m_classIdClass.append("import java.util.Map;").append(NEWLINE).
                append("import java.util.HashMap;").append(NEWLINE).append(NEWLINE);
        m_classIdClass.append("public class GeneratedClassID {").append(NEWLINE);
        m_classIdClass.append("\tprivate Map<String, Integer> classIdMap;").append(NEWLINE).append(NEWLINE);
        m_classIdClass.append("\tpublic GeneratedClassID() {").append(NEWLINE).
                append("\t\tclassIdMap = new HashMap<String, Integer>();").append(NEWLINE).append(NEWLINE);

        int count = 0;
        for (String m_string : FULLCLASSNAMELIST) {
            m_classIdClass.append("\t\tclassIdMap.put(\"").append(m_string).append("\", ").append(count).append(");").
                    append(NEWLINE);
            count++;
        }
        m_classIdClass.append("\t}").append(NEWLINE).append(NEWLINE);
        m_classIdClass.append("\tpublic Map getClassIdMap() {").append(NEWLINE).append("\t\treturn classIdMap;").
                append(NEWLINE).append("\t}").append(NEWLINE).append(NEWLINE);

        m_classIdClass.append("}");

        printer.println(m_classIdClass);

        printer.close();

        dynamicCompileAndReloadClassIDClass(m_classIdClassPath);

    }

    private void dynamicCompileAndReloadClassIDClass(String packagePath) throws Exception {
        //dynamic compile class
        String output = "output" + FILESEPARATOR + "codeGen";

        int errorCode = com.sun.tools.javac.Main.compile(new String[]{
                //"-classpath", "bin",
                "-d", output,
                CODEGENDIRECTORY + FILESEPARATOR + packagePath + FILESEPARATOR + "GeneratedClassID.java"});

        if (errorCode != 0)
            throw new Exception("dynamic compiling error for GeneratedClassID.java");

        //dynamic load class
        URLClassLoader loader = new URLClassLoader(
                new URL[]{new File(output + FILESEPARATOR).toURI().toURL()});
        loader.loadClass(packagePath.replace(System.getProperty("file.separator").charAt(0), '.') + ".GeneratedClassID");

    }
   /*
    private void createHibernateMappingFiles() throws IOException {
        MAPPINGFILENAMELIST.clear();
        for (Class m_class : CLASSLIST) {
            if (!m_class.isEnumType() && !m_class.isDataType()) {
                File m_mappingfile = new File(PERSISTENTDIRECTORY + FILESEPARATOR + m_class.getOwnerPackage().getNameSpacePath(),
                        m_class.getName() + ".hbm.xml");
                m_mappingfile.createNewFile();

                MAPPINGFILENAMELIST.add(m_class.getOwnerPackage().getNameSpacePath() + FILESEPARATOR + m_class.getName() + ".hbm.xml");

                new XtalkModelMappingFileCreator(m_class, m_mappingfile).writeClassToMappingFile();
            }
        }
    }
    */
    private void createHibernateConfigFile() throws IOException {
        File m_configFile = new File(CONFIGDIRECTORY, "persistence.xml");
        if (!m_configFile.createNewFile()) {
            //delete the old config file, then recreate it
            m_configFile.delete();
            m_configFile.createNewFile();
        }
        new XtalkModelConfigFileCreator(m_configFile).createConfigFile();
    }


    public void generateSanityCheckReport() throws IOException {
        StringBuilder reportContent = new StringBuilder("<html><head><title>Xtalk Model Sanity Check</title></head><body>");
        reportContent.append("<h3>Here is a list of classes who are not inherited from Element</h3><br>");

        for (Class m_class : CLASSLIST) {
            if (!m_class.isEnumType() && !m_class.isDataType()) {
                if (m_class.getParentClass() == null && !m_class.getName().equals("Element")) {
                    reportContent.append(m_class.getOwnerPackage().getNameSpacePath().replace(System.getProperty("file.separator").charAt(0), '.')).
                            append(".").append(m_class.getName()).append("<br>");
                }
            }
        }

        reportContent.append("<h3>Here is a list of associationEnd whose first charactor is uppercase</h3><br");
        for (AssociationEnd m_end : ASSOCIATIONENDLIST) {
            if (Character.isUpperCase(m_end.getName().charAt(0))) {
                reportContent.append(m_end.getName()).append("<br>");
            }
        }

        reportContent.append("<h3>Here is a list of all Classes with their full packages</h3>");
        for (String m_string : FULLCLASSNAMELIST) {
            reportContent.append(new StringBuilder(m_string).append("<br>"));
        }

        reportContent.append("</body></html>");

        File reportFile = new File(PERSISTENTDIRECTORY, "sanityCheckReport.html");
        reportFile.createNewFile();
        PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)), true);
        printer.println(reportContent.toString());
        printer.close();
    }

    public static void main(String argv[]) {
        try {
            XtalkModelParser m_parser = new XtalkModelParser();
            XtalkModelGenerator m_generator = new XtalkModelGenerator();

            m_parser.Process();

            m_generator.createPackagesAndClasses();

            m_generator.createHibernateConfigFile();

            m_generator.generateSanityCheckReport();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
