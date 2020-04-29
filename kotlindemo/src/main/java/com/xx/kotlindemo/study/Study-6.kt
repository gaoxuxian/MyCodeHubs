package com.xx.kotlindemo.study

class Study6 {
    /**
     * 进制数 (与Java有区别)
     *
     * 支持：
     *      1、十六进制 (0x)
     *      2、十进制
     *      3、二进制   (0b)
     *      4、八进制 (Kotlin 不支持，Java支持)
     */
    val hex = 0xf
    val dec = 16
    val bin = 0b0001_0000
}

fun main() {
    val study = Study6()

    println("十六进制数 hex => ${study.hex}")
    println("十进制数 dec => ${study.dec}")
    println("二进制数 bin => ${study.bin}")
}