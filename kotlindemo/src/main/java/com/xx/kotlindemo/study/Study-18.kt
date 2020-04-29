package com.xx.kotlindemo.study

/**
 * 类与继承 part five
 *
 * 匿名内部类、嵌套类、内部类
 */
open class AbsBase {
    open fun f() {}
}

abstract class AbsDerived : AbsBase() {
    abstract fun v()
}

class Outer {
    private val bar: Int = 1

    class Nested {
        /**
         * 嵌套类
         */
        fun foo() = 2
    }

    inner class Inner {
        /**
         * 内部类
         */
        fun foo() = bar
    }
}

fun main() {

    // 匿名内部类
    val absDerived = object : AbsDerived() {
        override fun v() {
            println("这是我实现的v()")
        }

        override fun f() {
            println("这是我实现的f()")
        }
    }

    absDerived.v()
    absDerived.f()

    val a = Outer.Nested().foo() // == 2

    val b = Outer().Inner().foo() // == 1
}