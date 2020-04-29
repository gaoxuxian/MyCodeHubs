package com.xx.kotlindemo.study

/**
 * 委托
 */
interface WTBase {
    val message: String
    fun print()
}

class WTBaseImpl(val x: Int): WTBase {
    override val message = "BaseImpl: x = $x"
    override fun print() {
        println(x)
    }
}

class WTDerived(b: WTBase) : WTBase by b {
    // 在 b 的 `print` 实现中不会访问到这个属性
    override val message = "Message of Derived"
}

fun main() {
    val b = WTBaseImpl(10)
    val derived = WTDerived(b)
    derived.print()
    println(derived.message) // 委托对象的成员只能访问其自身对接口成员实现
}