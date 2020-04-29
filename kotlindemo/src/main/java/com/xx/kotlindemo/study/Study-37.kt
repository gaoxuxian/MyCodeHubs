package com.xx.kotlindemo.study


/**
 * 内联函数
 *
 *
 */
fun grally() {
    val x = 8
    val y = x * x
    println("y = $y")
}

inline fun grally1() {
    val x = 7
    val y = x * x
    println("y = $y")
}

fun main() {
    grally()
    grally1()
}