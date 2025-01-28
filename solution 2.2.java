import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import py4j.GatewayServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaParserService {

    // Enum to represent different types of statements
    enum StatementType {
        METHOD_CALL,        // method calls
        CONDITIONAL_EXPR,   // Conditional expression [Branching]
        DO_STMT,            // Do .. while statement  [Loop]
        FOR_STMT,           // For .. Loop Statement  [Loop]
        FOREACH_STMT,       // For ... Each Statment  [Loop] 
        IF_STMT,            // If .. Else Statment    [Branching]
        SWITCH_STMT         // Switch Statmen         [Branching]
    }

    // This is the method that is called by python script via temporary Py4j JVM Server. Method name should exactly match
    public Map<String, List<MethodCall>> parseJavaFile(String filePath) {
        
        Map<String, List<MethodCall>> methodInvocations = new HashMap<>();    // Container to hold methods and its invocations
        
        try {
            CompilationUnit cu = StaticJavaParser.parse(new File(filePath));  // Parse the Java source file: Creates the AST (Abstract Syntax Tree)  of a file
            
            // Important Line
            // an instance of the MethodVisitor class, which is a visitor that will traverse the AST of the source file.
            cu.accept(new MethodVisitor(), methodInvocations);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Returns a map where method names as keys and lists of <<MethodCall objects as values>>, This will be dumped to Python Script
        return methodInvocations;
    }

    // A static inner class
    private static class MethodVisitor extends VoidVisitorAdapter<Map<String, List<MethodCall>>> {
        
        @Override
        public void visit(MethodDeclaration md, Map<String, List<MethodCall>> methodInvocations) {

            // Didnt really understand this part, referring to the book, it says that it is good practice to ensure any necessary actions defined in the superclass
            super.visit(md, methodInvocations); 
            
            List<MethodCall> methodCalls = new ArrayList<>(); //to store MethodCall objects representing method calls found within the current method.

            // Checks if the method has a body (i.e., it is not abstract or an interface method).
            // if the body is present, the lambda expression process it (much simpler this way)
            md.getBody().ifPresent(body -> {

                // Finds all method call expressions (MethodCallExpr) in the method body.
                body.findAll(MethodCallExpr.class).forEach(mc ->
                        methodCalls.add(new MethodCall(mc.getNameAsString(), StatementType.METHOD_CALL)) // Look for the statment type that is method_call (Method invocation) and store it
                );

                // Finds all conditional expressions (ConditionalExpr) in the method body
                body.findAll(ConditionalExpr.class).forEach(ce ->
                        methodCalls.addAll(findMethodCallsInExpression(ce)) 
                );

                // Finds all Do while statment [Loop]
                body.findAll(DoStmt.class).forEach(doStmt ->
                        methodCalls.addAll(findMethodCallsInStatement(doStmt.getBody(), StatementType.DO_STMT))
                );

                // Finds For ..  statment [Loop]
                body.findAll(ForStmt.class).forEach(forStmt ->
                        methodCalls.addAll(findMethodCallsInStatement(forStmt.getBody(), StatementType.FOR_STMT))
                );

                // Finds For each ..  statment [Loop]
                body.findAll(ForEachStmt.class).forEach(foreachStmt ->
                        methodCalls.addAll(findMethodCallsInStatement(foreachStmt.getBody(), StatementType.FOREACH_STMT))
                );

                // Finds If..else  statment [Branching]
                body.findAll(IfStmt.class).forEach(ifStmt -> {
                    methodCalls.addAll(findMethodCallsInStatement(ifStmt.getThenStmt(), StatementType.IF_STMT));
                    ifStmt.getElseStmt().ifPresent(elseStmt ->
                            methodCalls.addAll(findMethodCallsInStatement(elseStmt, StatementType.IF_STMT))
                    );
                });

                // Finds Switch ..  statment [Loop]
                body.findAll(SwitchStmt.class).forEach(switchStmt ->
                        switchStmt.getEntries().forEach(switchEntry ->
                                methodCalls.addAll(findMethodCallsInStatements(switchEntry.getStatements(), StatementType.SWITCH_STMT))
                        )
                );
                
            });

            // Puts the list of MethodCall objects into the methodInvocations map with the method name as the key.
            methodInvocations.put(md.getNameAsString(), methodCalls); 
        }

        // processes a ConditionalExpr object to find all method calls within it.
        private List<MethodCall> findMethodCallsInExpression(ConditionalExpr expr) {
            
            List<MethodCall> methodCalls = new ArrayList<>();

            // Finding Method Calls in the Condition: (IF statement)
            expr.getCondition().findAll(MethodCallExpr.class).forEach(mc ->
                    methodCalls.add(new MethodCall(mc.getNameAsString(), StatementType.CONDITIONAL_EXPR))
            );

            // Finding Method Calls in the Condition: (Elseif statement)
            expr.getThenExpr().findAll(MethodCallExpr.class).forEach(mc ->
                    methodCalls.add(new MethodCall(mc.getNameAsString(), StatementType.CONDITIONAL_EXPR))
            );

             // Finding Method Calls in the Condition: (Else statement)
            expr.getElseExpr().findAll(MethodCallExpr.class).forEach(mc ->
                    methodCalls.add(new MethodCall(mc.getNameAsString(), StatementType.CONDITIONAL_EXPR))
            );
            
            return methodCalls;
            
        }
        
        // to find method calls within a given Statement object, handling both individual statements and blocks of statements
        private List<MethodCall> findMethodCallsInStatement(Statement stmt, StatementType type) {
            
            List<MethodCall> methodCalls = new ArrayList<>();
            
            if (stmt.isBlockStmt()) {
                
                BlockStmt blockStmt = stmt.asBlockStmt();
                blockStmt.getStatements().forEach(s ->
                        methodCalls.addAll(findMethodCallsInStatement(s, type))
                );
                
            } else {
                
                stmt.findAll(MethodCallExpr.class).forEach(mc ->
                        methodCalls.add(new MethodCall(mc.getNameAsString(), type))
                );
                
            }
            
            return methodCalls;
            
        }

        private List<MethodCall> findMethodCallsInStatements(List<Statement> statements, StatementType type) {
            
            List<MethodCall> methodCalls = new ArrayList<>();
            statements.forEach(stmt ->
                    methodCalls.addAll(findMethodCallsInStatement(stmt, type))
            );
            
            return methodCalls;
            
        }
    }

    // Class to represent a method call with its type
    static class MethodCall {
        
        private String methodName;  // Stores the name of the method.
        private StatementType type; // Stores the type of statement (indicating where the method call was found, e.g., CONDITIONAL_EXPR, BLOCK, Loop or Branching).


        public MethodCall(String methodName, StatementType type) {
            
            this.methodName = methodName;
            this.type = type;
            
        }

        public String getMethodName() {
            return methodName;
        }

        public StatementType getType() {
            return type;
        }

        @Override
        public String toString() {
            return methodName + " (" + type + ")";
        }
    }

    // starts a gateway server using the GatewayServer class,
    public static void main(String[] args) {
        
        JavaParserService app = new JavaParserService();
        GatewayServer server = new GatewayServer(app);
        server.start();
        System.out.println("Gateway Server Started! OH YES!");
        
    }
}
