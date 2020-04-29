package com.xx.kotlindemo.study

/**
 * 泛型 part one
 *
 * 简单使用
 */
class Generic1<T>(t: T) {
    var value = t
}

fun main() {
    val intGeneric = Generic1<Int>(123)
    println(intGeneric.value)

    val intGeneric1 = Generic1(321)
    println(intGeneric1.value)
    println(intGeneric.value.plus(100))

    val stringGeneric = Generic1("Generic")
    println(stringGeneric.value)
    println(stringGeneric.value.length)

    // 可空类型一定要说明泛型类型
    val stringGeneric1 = Generic1<String?>(null)
    println(stringGeneric1.value)
    println(stringGeneric1.value?.length)
}