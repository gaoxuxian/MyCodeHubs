package com.xx.javademo.fanxing;

import java.util.ArrayList;

/**
 * 泛型数组demo
 * <p>
 * 知乎：java为什么不支持泛型数组?
 * https://www.zhihu.com/question/20928981/answer/117521433
 */
public class CaseV1 {
    private ArrayList<Integer>[] test;

    {
        // 从知乎链接的大神回复，知道 Java直接禁止了具体泛型数组的初始化
        // 1.
        // test = new ArrayList<Integer>[3]; // 编译报错

        // 2. new 一个原生类型（ArrayList）数组，赋值给具体泛型数组引用，在编译器看来是合法的
        // test = new ArrayList[3]; //
        // test = (ArrayList<Integer>) new ArrayList[3]; // 编译通过

        // 但是为什么呢？
        // 其实针对第1点，编译器直接报错的原因，Java语言规范是有规定的
        /*
            引用知乎答案：
                Java Language Specification（Java语言规范）明确规定：数组内的元素必须 不是reifiable的。
                It is a compile-time error if the component type of the array being initialized is not reifiable.
            对reifiable的第一条定义就是不能是泛型：
                A type is reifiable if and only if one of the following holds:
                It refers to a non-generic class or interface type declaration
         */

        // 虽说有这样的规定，但基于泛型的擦除实现，在class文件中，第1点的 new ArrayList<Integer>[3]; 和 第2点的 new ArrayList[3]; 难道不是等价的吗？
        // 请看以下例子：
            // 4、编译通过
            // test = new ArrayList[4];
            // 5、编译报错
            // test = new ArrayList<Object>[4];
        // 如果1、2是等价，那么4、5应该也是等价，并且5不会编译报错，但结果并不是。
        // 所以对于JVM而言，new ArrayList[4]; 是not reifiable. 但 new ArrayList<Object>[4]; 不是not reifiable.

        // 通过以上例子，足以说明Java为什么不支持泛型数组的初始化，为什么说第1点的规定是为了不破坏类型安全？
        // 请看以下例子：

        ArrayList<Integer> intList = new ArrayList<>();
        intList.add(1);
        intList.add(2);
        intList.add(3);

        ArrayList<String> stringList = new ArrayList<>();
        stringList.add("4");
        stringList.add("5");
        stringList.add("6");

        ArrayList<Float> floatList = new ArrayList<>();
        floatList.add(7f);
        floatList.add(8f);
        floatList.add(9f);

        // 由于 test 定义为 ArrayList<Integer>，所以对数组元素ArrayList取值时，自动强转Integer，但传入数组的元素，并不一定是ArrayList<Integer>
        // 则可能导致 java.lang.ClassCastException 异常
        test = new ArrayList[]{intList, stringList, floatList};
        Integer integer = test[0].get(0);
        Integer integer1 = test[1].get(0);
        Integer integer2 = test[2].get(0);
    }
}
