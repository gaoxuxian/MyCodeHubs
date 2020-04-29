package com.xx.kotlindemo.study

/**
 * 扩展 part four
 *
 * 扩展声明为成员
 */
class ExOutside {
    fun bar() {
        println("This is ExOutside")
    }
}

class Expand4 { // Expand4() --> 分发接收者
    fun baz() {
        println("This is Expand4 baz()")
    }

    private fun ExOutside.foo() { // ExOutside() --> 扩展接收者
        /**
         * 在一个类内部你可以为另一个类声明扩展。在这样的扩展内部，有多个 隐式接收者 —— 其中的对象成员可以无需通过限定符访问
         *
         * 扩展声明所在的类的实例称为 分发接收者，扩展方法调用所在的接收者类型的实例称为 扩展接收者
         */
        bar() // 调用 ExOutside.bar 相当于 this.bar()
        baz() // 调用 Expand4.baz 相当于 this@Expand4.baz()
    }

    private fun ExOutside.ToStr() {
        // 对于 分发接收者 与 扩展接收者 的成员名字冲突的情况，扩展接收者优先
        // 可以用 this 区分
        println(toString())
        println(this@Expand4.toString())
    }

    fun caller(exOutside: ExOutside) {
        exOutside.foo()
        exOutside.ToStr()
    }
}

fun main() {
    Expand4().caller(exOutside = ExOutside())
}