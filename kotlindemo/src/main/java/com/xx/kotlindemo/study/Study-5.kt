package com.xx.kotlindemo.study

class Study5 {

    /**
     * 基础数据类型(与Java类似)
     *
     */
    val byte: Byte = 8 // 8位 --> 字节

    val short: Short = 16 // 16位 --> 2个字节
    val int: Int = 32 // 32位 --> 4个字节
    val long: Long = 64 // 64位 --> 8个字节

    val float: Float = 32.0f // 32位 --> 4个字节
    val double: Double = 64.0 // 64位 --> 8个字节

    var value = 0
}

fun main() {
    val study = Study5()

    println("byte 位数 => ${study.byte}")
    println("short 位数 => ${study.short}")
    println("int 位数 => ${study.int}")
    println("long 位数 => ${study.long}")
    println("float 位数 => ${study.float}")
    println("double 位数 => ${study.double}")

    println("study.value => ${study.value}")
    study.value = 1
    println("study.value => ${study.value}")
}