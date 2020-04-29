package com.xx.kotlindemo.study

class Study12 {
    /**
     * for 循环
     *
     * Kotlin废除了Java中的for(初始值;条件；增减步长)这个规则。但是Kotlin中对于for循环语句新增了其他的规则，来满足刚提到的规则。
     *
     * for循环提供迭代器用来遍历任何东西
     *
     * for循环数组被编译为一个基于索引的循环，它不会创建一个迭代器对象
     */

    fun printUntil() {
        println("===================== until =====================")
        for (i in 0 until 6) { // until --- [a, b) 递增
            println("i ---> $i")
        }
    }

    fun printDownTo() {
        println("===================== downTo =====================")
        for (i in 10 downTo 5) { // downTo --- [a, b] 递减
            println("i ---> $i")
        }
    }

    fun printTwoPoint() {
        println("===================== .. =====================")
        for (i in 90..98) { // .. --- [a, b] 递增
            println("i ---> $i")
        }
    }

    fun printStep() {
        println("===================== until step =====================")
        for (i in 9 until 15 step 3) { // step 步长 --- a与b的差值，第一个输出9，第二个输出12
            println("i ---> $i")
        }

        println("===================== downTo step =====================")
        for (j in 15 downTo 9 step 2) {
            println("j ---> $j")
        }

        println("===================== .. step =====================")
        for (k in 15 .. 20 step 1) {
            println("k ---> $k")
        }
    }


    fun printIteration() {
        println("===================== iteration string =====================")
        val string = "abcdefg..hij!!"
        /**
         * for 循环数组被编译为一个基于索引的循环，它不会创建一个迭代器对象
         */
        for (s in string) {
            print("s --> $s \t")
        }

        println()
        println("===================== iteration string iterator() =====================")
        for (c in string.iterator()) {
            print("c --> $c \t")
        }

        println()
        println("===================== iteration value array =====================")

        val arrayValue = arrayOf(1, 2, 4, 5, 3, 6)
        for (i in arrayValue) {
            print("i --> $i \t")
        }

        println()
        println("===================== iteration value array iterator() =====================")

        for (j in arrayValue.iterator()) {
            print("j --> $j \t")
        }

        println()
        println("===================== iteration value array indices =====================")

        for (k in arrayValue.indices) {
            print("k --> $k \t")
        }

        println()
        println("===================== iteration value array withIndex() =====================")

        for (y in arrayValue.withIndex()) {
            println("y --> $y \t" + "y.index --> ${y.index} \t" + "y.value --> ${y.value}")
        }
    }
}

fun main() {
    val study = Study12()

    study.printUntil()

    study.printDownTo()

    study.printTwoPoint()

    study.printStep()

    study.printIteration()
}