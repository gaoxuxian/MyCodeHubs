package com.xx.kotlindemo.study

class Study4 {
    /**
     * 后期初始化 ---> 关键字 lateinit
     *
     * 在 Kotlin 中不为空的对象必须初始化。如果不想定义的时候初始化，那么怎么办呢？可以输入 lateinit。来承诺最终将会初始化。
     *
     * 定义：
     *      lateinit var 变量名: 类型
     *
     * 注意事项:
     *      1、必须使用 lateinit 关键字
     *
     *      2、必须是 var 变量
     *
     *      3、不能声明 可空变量、基础变量(Int、Long、Float、Double等)，String 类型是可以的
     *
     *      4、声明后，在使用变量前，必须赋值，不然会抛出 UninitializedPropertyAccessException
     */

    lateinit var value: String
}

fun main() {
    val study = Study4()

//    println("study.value = ${study.value}")

    study.value = "初始化了"

    println("study.value = ${study.value}")
}