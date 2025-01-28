package org.example;

public class Experiment2 {

    public static void main(String[] args) {
        try {
            InterfaceB b = new ClassB();
            InterfaceC c = new ClassC();
            ClassA a = new ClassA(b, c);
            a.methodA();
        } catch (Exception e) {
            System.out.println("Error caught in main: " + e.getMessage());
        }
    }

}
