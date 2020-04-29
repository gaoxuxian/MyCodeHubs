package com.xx.kotlindemo.study

class Study1 {

    /**
     * 属性与字段
     *
     * Kotlin ：属性不一定分配存储空间，关键在于属性是否具备幕后字段
     * Java：没有属性的概念，都是字段，定义好的字段，有默认值，且会分配存储空间
     *
     * 如果属性至少一个访问器使用默认实现，或者自定义访问器通过 field 引用幕后字段，将会为该属性生成一个幕后字段。
     *
     * var 和 val 的区别、定义
     *
     * var / val 区别：
     *      var ---> 可变，可读可写，相当于 Java 普通变量
     *      val ---> 不可变，可读不可写，相当于 Java final 变量
     *
     * var / val 定义：
     *      1、var/val 变量名: 类型 = 确定值
     *      2、var/val 变量名 = 确定值 ---> 情况1的简写，自动推断类型
     *      3、var/val 变量名: 类型 ---> 情况1的特殊个例
     */

    /**
     * value1 和 value2 是属性，且有幕后字段
     */
    var value1: Int = 10 // 可变变量，可读可写，相当于 Java 普通变量
//   var varFirst = 10 // 可变变量，自动推导类型
//   var varFirst: Float // 没有初始值时，必须声明类型，同时需要在构造函数赋值

    val value2: Double = 20.02 // 不可变变量，可读不可写，相当于 Java final 变量
//   val valFirst = 20.02 // 不可变变量，自动推导类型
//   val valFirst: Double // 没有初始值时，必须声明类型，同时需要在构造函数赋值

    /**
     * value3 和 value4 是属性，但没有幕后字段，相当于只有 getter、setter 方法而已
     *
     * {转成 Java 代码更容易理解}
     */
    var value3
        get() = 3
        set(value) {
            println("set value3 = $value")
        }

    val value4
        get() = 4
}

fun main() {
    val study = Study1()

    println("study.varValue = ${study.value1}")
    study.value1 = 11
    println("study.varValue = ${study.value1}")

    println("study.valValue = ${study.value2}")
}