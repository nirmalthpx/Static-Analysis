package org.example;

import sootup.core.graph.StmtGraph;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.model.SootMethod;
import sootup.core.signatures.MethodSignature;
import sootup.core.signatures.MethodSubSignature;
import sootup.core.util.DotExporter;
import sootup.java.bytecode.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaSootClass;
import sootup.java.core.types.JavaClassType;
import sootup.java.core.views.JavaView;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Optional;

public class AnalyseCode {


    public static void main(String[] args) {


        String[] handlers;

        handlers = new String[] {"simpleLoop", "nestedLoop", "simpleBranch", "nestedBranch", "switchCase"};

        for (String handler : handlers) {

            System.out.println(handler);
            generateProgramFlow(handler);

        }

    }

    public static void generateProgramFlow(String handler){

        // pointing towards the compiled classes
        AnalysisInputLocation inputLocation =
                new JavaClassPathAnalysisInputLocation("target/classes");

        // creating  (this will be stored in Cache, this can be changed how view is stored in cache)
        JavaView view = new JavaView(inputLocation);

        // Defining a class Type
        JavaClassType classType =
                view.getIdentifierFactory().getClassType("org.example.Experiment");

        // Retrieving a SootClass
        JavaSootClass sootClass = view.getClass(classType).get();

        MethodSignature methodSignature = view.getIdentifierFactory().getMethodSignature(
                classType,
                handler,
                "void",
                Collections.singletonList("java.lang.String[]"));

        // retrieving a SootMethod from the view
        view.getMethod(methodSignature);

        // Alternatively
        SootMethod sootMethod = sootClass.getMethod(methodSignature.getSubSignature()).get();

        // read jimple code of method
        //System.out.println(sootMethod.getBody());

        // ===== Retrieving the Control-Flow Graph of a Method
        StmtGraph<?> graph = sootMethod.getBody().getStmtGraph();

        String urlToWebeditor = DotExporter.createUrlToWebeditor(graph);

        System.out.println(urlToWebeditor);

    }

}
