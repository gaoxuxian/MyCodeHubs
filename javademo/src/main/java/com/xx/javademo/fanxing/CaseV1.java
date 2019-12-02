package com.xx.javademo.fanxing;

import java.util.ArrayList;

/**
 * 泛型数组demo
 * <p>
 * 知乎：java为什么不支持泛型数组?
 * https://www.zhihu.com/question/20928981/answer/117521433
 */
public class FanXing1 {
    private ArrayList<Integer>[] test;

    {
        // 1.直接 new 具体泛型类型的数组，编译器会报错
        // test = new ArrayList<Integer>[3];
    }

    {
        /*
            从 上述第1点 和 知乎链接的大神回复，我们知道 Java直接禁止了具体泛型数组的初始化
            同时，new 一个原生类型数，赋值给具体泛型数组引用，在编译器看来是合法的
         */
        // test = new ArrayList[3];

        /*
            但这是为什么呢？
            其实针对第1点，编译器直接报错的原因，是不希望破坏类型安全。
         */

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

        /*
        那么
         */
        test = new ArrayList[]{intList, stringList, floatList};
    }
}
