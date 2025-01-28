package org.example;

import java.util.Date;

public class Experiment {

    private static int countA = 0;
    private static int countB = 0;
    private static int countC = 0;
    private static int countD = 0;
    private static int countE = 0;
    private static int countF = 0;
    private static int countG = 0;
    private static int countH = 0;

    public static void main(String[] args) {

        for (int i = 0; i < 100; i++) { // Run multiple iterations to simulate profiling
            try {
                a();
            } catch (Exception e) {
                System.out.println("Error caught in main: " + e.getMessage());
            }
        }
        printProfileData();

    }

    // Sample method having branching statement
    public static void a() throws Exception {
        countA++;
        if (Math.random() > 0.5) {
            b();
        } else {
            c();
        }
        d();
    }

    // Sample method having loop statement
    public static void b() throws Exception {
        countB++;
        for (int i = 0; i < 1000000; i++) {
            e();
        }
        g();
    }

    public static void c() {
        countC++;
    }

    public static void d() throws Exception {
        countD++;
        e();
        f(1000);
        h();
    }

    public static void e() {
        countE++;
    }

    public static void deadMethod() throws Exception {
        d();
    }

    // Sample recursive method
    public static void f(int n) throws Exception {
        countF++;
        if (n > 0) {
            f(n - 1);
        }
    }

    // Sample method that throws an exception
    public static void g() throws Exception {
        countG++;
        throw new Exception("Error in g");
    }

    // New method that is frequently called
    public static void h() {
        countH++;
        i();
    }

    // Print profiling data to know Potential Performance Bottlenecks
    public static void printProfileData() {
        System.out.println("Profile Data:");
        System.out.println("a() called " + countA + " times");
        System.out.println("b() called " + countB + " times");
        System.out.println("c() called " + countC + " times");
        System.out.println("d() called " + countD + " times");
        System.out.println("e() called " + countE + " times");
        System.out.println("f() called " + countF + " times");
        System.out.println("g() called " + countG + " times");
        System.out.println("h() called " + countH + " times");
    }

    public static void i(){
        Date date = new Date();
        int year = date.getYear(); // Deprecated method
        System.out.println("Year: " + year);
    }
}
