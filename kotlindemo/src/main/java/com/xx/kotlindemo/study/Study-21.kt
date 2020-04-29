package com.xx.kotlindemo.study

/**
 * 扩展 part one
 *
 * Kotlin 同 C# 与 Gosu 类似，能够扩展一个类的新功能而无需继承该类或使用像装饰者这样的任何类型的设计模式。
 *
 * 这通过叫做 扩展 的特殊声明完成。Kotlin 支持 扩展函数 与 扩展属性。
 */
class Expand1 {
    val x = 123
}

/**
 * 扩展函数
 *
 * 声明一个扩展函数，我们需要用一个 接收者类型 也就是被扩展的类型来作为他的前缀
 */
fun Expand1.transform(): String {
    return this.x.toString() + ".com"// this 对应 Expand1 的实例
}

fun main() {
    val expand = Expand1()
    println(expand.transform())
}