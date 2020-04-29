package com.xx.kotlindemo.study

/**
 * 接口 part one
 *
 * Kotlin 的接口与 Java 8 类似，既包含抽象方法的声明，也包含实现。
 *
 * 与抽象类不同的是，接口无法保存状态。它可以有属性但必须声明为抽象或提供访问器实现
 */
interface IBase {
    var x: Int // 在接口内，是抽象的，并且没有幕后字段，只是 getter\setter 方法
    val name: String // 在接口内，是抽象的，并且没有幕后字段，只是 getter 方法

    // 没有实现的方法
    fun bar()

    // 在接口内，有方法体的方法，自带默认实现
    fun foo() {
        println("默认实现的接口方法 foo()")
    }
}

/**
 * 情况一: 抽象属性生成 幕后字段
 */
class BaseImpl1 : IBase {
    // 实现接口时，重写属性，并使用默认实现的 getter\setter 方法
    override var x: Int = 1
    override val name: String = "BaseImpl1"

    override fun bar() {
        println("This is BaseImpl1 bar()")
    }
}

// 实现接口时，在构造函数中重写属性，自动生成幕后字段
class BaseImpl2(override var x: Int, override val name: String) : IBase {
    override fun bar() {
        println("This is BaseImpl2 bar()")
    }
}

/**
 * 情况二：抽象属性不生成 幕后字段
 */
class BaseImpl3: IBase {
    override var x: Int
        get() = 3
        set(value) {}

    override val name: String
        get() = "BaseImpl3"

    override fun bar() {
        println("This is BaseImpl3 bar()")
    }
}

fun main() {
    var b: IBase = BaseImpl1()
    println(b.x)
    println(b.name)
    b.bar()
    b.foo()
    println()

    b = BaseImpl2(2, "BaseImpl2")
    println(b.x)
    println(b.name)
    b.bar()
    b.foo()
    println()

    b = BaseImpl3()
    println(b.x)
    println(b.name)
    b.bar()
    b.foo()
}