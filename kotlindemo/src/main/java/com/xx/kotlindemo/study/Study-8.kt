package com.xx.kotlindemo.study

class Study8 {

    /**
     * 位运算 ---> Kotlin 中没有 Java 的位运算符，用的是函数形式，同时位运算仅适用于 Int 和 Long
     *
     * 函数列表：
     *          shl() ---> 有符号 左移 (类似 Java 的 << )
     *          shr() ---> 有符号 右移 (类似 Java 的 >> )
     *          ushr() ---> 无符号 右移 (类似 Java 的 >>>)
     *          and() ---> 位运算 and (同 Java 的 按位与) ---> 同为 1 则 1
     *          or() ---> 位运算 or (同 Java 的 按位或 ) ---> 有 1 则 1
     *          xor() ---> 位运算 xor (同 Java 的 按位异或 ) ---> 不同则1，相同则0
     *          inv() ---> 位运算 inv (同 Java 的 按位取反 ) ---> 1 => 0, 0 => 1
     */
    val bit = 4
}

fun main() {
    val study = Study8()

    println("位运算初始值study.bit：${study.bit}")

    println("study.bit.shl(2) => ${study.bit.shl(2)}")

    println("study.bit.shr(2) => ${study.bit.shr(2)}")
    println("study.bit.ushr(2) => ${study.bit.ushr(2)}")

    println("study.bit.and(2) => ${study.bit.and(2)}")

    println("study.bit.or(2) => ${study.bit.or(2)}")

    println("study.bit.xor(2) => ${study.bit.xor(2)}")

    println("study.bit.inv() => ${study.bit.inv()}")
}