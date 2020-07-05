package com.xx.javademo.fanxing;

public class CaseV2 {
    public static class ABC {

    }

    public static class A extends ABC {

    }

    public static class B extends ABC {

    }

    public static class C extends ABC {

    }

    public static class a extends A {

    }

    public static class MyTest<T> {
        private T t;

        public T get() {
            return t;
        }
    }

    public void test() {
        MyTest<? super a> test = new MyTest<>();
        Object object = test.get();
    }
}