package com.xx.kotlindemo.study

/**
 * 接口 part two
 */
interface IA {
    fun foo() {
        println("This is IA foo()")
    }
    fun bar()
}

interface IB {
    fun foo() {
        println("This is IB foo()")
    }
    fun bar() {
        println("This is IB bar()")
    }
}

interface IC:IA {
    override fun foo() {
        println("This is IC foo()")
    }
    fun c()
}

class ABImpl: IA, IB {
    override fun bar() {
        super<IB>.bar()
    }

    override fun foo() {
        super<IA>.foo()
        super<IB>.foo()
    }
}

class BCImpl: IB, IC {
    override fun bar() {
        super<IB>.bar()
    }

    override fun foo() {
        super<IC>.foo()
        super<IB>.foo()
    }

    override fun c() {
        println("This is ABCImpl c()")
    }
}

fun main() {
    val bcImpl = BCImpl()

    bcImpl.foo()
    bcImpl.c()
    bcImpl.bar()
    println()

    val abImpl = ABImpl()
    abImpl.foo()
    abImpl.bar()
}