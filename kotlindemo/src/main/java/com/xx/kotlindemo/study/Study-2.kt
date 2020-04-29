package com.xx.kotlindemo.study

class Study2 {
    /**
     * 可空变量
     *
     * 定义：
     *      var/val 变量名 : 类型? = null / 确定的值
     *
     * 注意事项：
     *      1、一定要用标准的声明格式定义，不能用可推断类型的简写
     *      2、变量类型后面的 ? 不能省略
     *      3、其初始化的值可以为 null 或 确定的值
     */
    var value: String? = null

    fun printNull1() {
        println("===================== Java 判null =====================")
        if (value == null) {
            println("study.value 是 null")
        } else {
            println(value)
        }
        println()
    }

    fun printNull2() {
        println("===================== 使用 ?. 判null =====================")
        // ?. --> 执行安全调用（如果接收者非空，就调用一个方法或访问一个属性）
        println("study.value --> $value, study.value.length --> ${value?.length}")
        value = "1234567"
        println("study.value --> $value, study.value.length --> ${value?.length}")
        println()
    }

    fun printNull3() {
        println("===================== 使用 let 避开null object =====================")
        val array: Array<String?> = arrayOf("a", "b", null, "d", null, "f")
        for (s in array) {
            s?.let { print("s --> $it \t") }
        }
        println()
    }

    fun printNull4() {
        println("===================== 使用 ?: 输出默认值 =====================")
        value = null
        // 操作符 ?: -->  如果左侧的值为空，就取右侧的值（elvis 操作符）
        println("study.value --> $value, study.value.length --> ${value?.length ?: -1}")
        println()
    }

    fun printNull5() {
        println("===================== 使用 as? 安全转换 =====================")
        value = "KotlinDemo"
        val result: Int? = value as? Int
        println("study.value --> $value, study.value is Int --> $result")
        println()
    }

    fun printNull6() {
        println("===================== 使用 !! 断言 =====================")
        // !! ---> 断言一个表达式非空
        println(value!!)
    }
}

fun main() {
    val study = Study2()

    study.printNull1()

    study.printNull2()

    study.printNull3()

    study.printNull4()

    study.printNull5()

    study.printNull6()
}

