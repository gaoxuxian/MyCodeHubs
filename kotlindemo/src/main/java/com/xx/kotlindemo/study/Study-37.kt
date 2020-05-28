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


fun foo() {
    ordinaryFunction {
        System.out.println(1)
        return@ordinaryFunction
    }
    ordinaryFunction2 {
        System.out.println(1)
        return
    }
    System.out.println(2)
}

fun ordinaryFunction(body: () -> Unit) {
    body.invoke()
}

inline fun ordinaryFunction2(body: () -> Unit) {
    body.invoke()
}

fun main() {
//    grally()
//    grally1()
    foo()
}
