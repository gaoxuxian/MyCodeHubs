package com.xx.kotlindemo.study

class Study11 {
    /**
     * if 判断
     *
     * 在Kotlin中的if语句和Java还是还是有一定的区别的，它能在Java中更灵活，除了能实现Java写法外，还可以实现表达式（实现三元运算符），及作为一个块的运用。
     */

    var a = 3
    var b: Int = if (a >= 2) 1 else 2 // 三项表达式
    var c: Int = if (b >= 3) {
        1
    } else if (b >= 2){
        2
    } else {
        3
    }
}

fun main() {
    val study = Study11()

    println("a: Int = ${study.a}")
    println("b: Int = ${study.b}")
    println("c: Int = ${study.c}")
}