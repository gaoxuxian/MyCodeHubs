package com.xx.kotlindemo.study

/**
 * 泛型 part four
 *
 * 泛型函数 与 泛型约束
 */

class Generic4 {
    fun <T> foo(t: T) {
        // 泛型函数
    }

    fun <T: Number> bar(b: T) {
        // 泛型约束，上界是 Number，默认是 Any? (不声明的时候)
        // 在尖括号中只能指定一个上界
    }

    /**
     * 如果同一类型参数需要多个上界，我们需要一个单独的 where-子句
     */
    fun <T> copyWhenGreater(list: List<T>, threshold: T): List<String>
            where T : CharSequence,
                  T : Comparable<T> {
        /**
         * 所传递的类型必须同时满足 where 子句的所有条件。在本例中，类型 T 必须既实现了 CharSequence 也实现了 Comparable。
         */
        return list.filter { it > threshold }.map { it.toString() }
    }
}

fun <T> Generic4.exFoo(t: T) {
    // 泛型扩展函数
}

fun main() {
    val g = Generic4()

    g.foo<Int>(1)

    g.exFoo<String>("123")
}