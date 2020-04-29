package com.xx.kotlindemo.study

/**
 * 类与继承 part three
 *
 * 如何调用父类成员
 */
open class Foo {
    open fun f() { println("Foo.f()") }
    open val x: Int get() = 1
}

class Bar : Foo() {

    /**
     * 派生类中的代码可以使用 super 关键字调用其超类的函数与属性访问器的实现
     */

    override fun f() {
        super.f()
        println("Bar.f()")
    }

    override val x: Int get() = super.x + 1

    inner class Baz {

        /**
         * 在一个内部类中访问外部类的超类，可以通过由外部类名限定的 super 关键字来实现：super@Outer
         */

        fun g() {
            super@Bar.f() // 调用 Foo 实现的 f()
            println(super@Bar.x) // 使用 Foo 实现的 x 的 getter
        }
    }
}

fun main() {
    val bar = Bar()
    println(bar.x)
    bar.f()

    val baz = bar.Baz()
    baz.g()
}