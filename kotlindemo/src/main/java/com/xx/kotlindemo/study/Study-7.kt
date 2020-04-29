package com.xx.kotlindemo.study

class Study7 {

    /**
     * 字符char 、转换
     *
     * 1、隐式转换 --> 类型从上下文推断，算术运算被重载为适当的转换
     *
     *      30L + 12 -> Long + Int => Long
     *
     * 2、显式转换 --> 较小的类型不会被隐式转换为更大的类型
     *
     *      toByte() => 转换为字节型
     *      toShort() => 转换为短整型
     *      toInt() => 转换为整型
     *      toLong() => 转换为长整型
     *      toFloat() => 转换为浮点型
     *      toDouble() => 转换为双精度浮点型
     *      toChar() => 转换为字符型
     *      toString() => 转换为字符串型
     *
     * 3、字符 ---> char 关键字，不能直接视为数字，要显式转换
     */
    val value: Char = 'a'
}

fun main() {
    val study = Study7()

    println("study.value == ${study.value}")

    println("study.value.toByte() == ${study.value.toByte()}")

    println("study.value.toShort() == ${study.value.toShort()}")
    println("study.value.toInt() == ${study.value.toInt()}")
    println("study.value.toLong() == ${study.value.toLong()}")

    println("study.value.toFloat() == ${study.value.toFloat()}")
    println("study.value.toDouble() == ${study.value.toDouble()}")

    println("study.value.toChar() == ${study.value.toChar()}")
    println("study.value.toString() == ${study.value.toString()}")

    println("study.value.toUpperCase() == ${study.value.toUpperCase()}")
    println("study.value.toLowerCase() == ${study.value.toLowerCase()}")
}