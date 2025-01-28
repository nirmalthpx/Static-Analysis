package org.example;

public class CustomLibrary {

    public static <T> void varDump(T value) {
        System.out.println("<<<<<<");
        System.out.println(value);
        System.out.println("Type of: " + value.getClass().getSimpleName());
        System.out.println(">>>>>>");
    }

}