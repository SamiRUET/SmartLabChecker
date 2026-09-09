package com.smartlab.parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.IfStmt;

public class PaeserTest {
    public static void main(String[] args) {

        String code = """
        class Calculator {

            int x;
            int y;

            int add(int a, int b) {
                return a + b;
            }

            void check(int n) {
                if (n > 10) {
                    System.out.println("Large");
                }
            }
        }
        """;

        CompilationUnit cu = StaticJavaParser.parse(code);

        var methods = cu.findAll(MethodDeclaration.class);
        var fields = cu.findAll(FieldDeclaration.class);
        var ifSegment = cu.findAll(IfStmt.class);

        for (var method : methods) {
            System.out.println("Method: " + method.getNameAsString());
            System.out.println("Return type: " + method.getType());
        }

        for (var field : fields) {
            for (var variable : field.getVariables()) {
                System.out.println("Field: " + variable.getNameAsString());
            }
        }

        System.out.println("Number of if statements: "
                + ifSegment.size());
    }
}