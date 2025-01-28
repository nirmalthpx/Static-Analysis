package org.example;

class ClassA {
    private InterfaceB b;
    private InterfaceC c;

    public ClassA(InterfaceB b, InterfaceC c) {
        this.b = b;
        this.c = c;
    }

    public void methodA() throws Exception {
        methodB();
        b.methodD();
    }

    public void methodB() throws Exception {
        for (int i = 0; i < 1000000; i++) {
            methodC();
        }
        c.methodE();
    }

    public void methodC() {
        // Do nothing
    }
}