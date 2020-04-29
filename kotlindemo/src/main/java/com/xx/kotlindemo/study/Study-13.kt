package com.xx.kotlindemo.study

class Study13 {
    /**
     * when
     *
     * 在Kotlin中已经废除掉了Java中的switch语句。而新增了when(exp){}语句。
     *
     * when语句不仅可以替代掉switch语句，而且比switch语句更加强大
     */

    fun printWhen1() {
        println("=============== normal switch ===============")
        val string = "a1"
        when(string) { // 不能是 char
            "a1" -> {
                print(string + 1)
            }
            "b" -> {
                print("b")
            }
            else -> {
                print(string)
            }
        }
        println()
    }

    fun printWhen2() {
        println("=============== when , ===============")
        val value = 4
        when(value) {
            1, 2, 3 -> {
                print(1)
            }
            5 -> {
                print(5)
            }
            else -> {
                print(value)
            }
        }
        println()
    }

    fun printWhen3() {
        println("=============== when in !in ===============") // 只适用于数值类型
        val array = arrayOf(1, 2, 3, 4, 10, 9, 8)
        when(5) {
            in array -> {
                print("5 存在于 array 中")
            }
            in 0..4 -> {
                print("5 属于 0~4 中")
            }
            !in 9 downTo 6 -> {
                print("5 不属于 6~9 中")
            }
            else -> {
                print("都错了")
            }
        }
        println()
    }

    fun printWhen4() {
        println("=============== when is !is ===============")
        val abc:String? = "abc"
        when(abc) {
            is String -> {
                print("abc 是字符串")
            }
            else -> {
                print("abc 不是字符串")
            }
        }
        println()

        val aInt:Int? = 2
        when(aInt) {
            !is Int -> {
                print("aInt 不是Int")
            }
            else -> {
                print("aInt = $aInt")
            }
        }
        println()
    }

    fun printWhen5() {
        println("=============== when 没有表达式 ===============")
        val arrayNull = arrayOfNulls<String>(3)
        when {
            true -> {
                for (i in arrayNull){
                    print(" $i \t")
                }
                println()
            }
            else -> {

            }
        }
    }
}

fun main() {
    val study = Study13()

    study.printWhen1()

    study.printWhen2()

    study.printWhen3()

    study.printWhen4()

    study.printWhen5()
}