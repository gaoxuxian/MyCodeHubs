package com.xx.kotlindemo.study

// :: 操作符的使用

fun (Int.(String) -> Unit).myTest(string: String, int: Int) {
    this.invoke(int, string)
}

fun test(int: Int) {
    println("这是test()方法")
}

object Te {
    var tep = 200

    fun Tee() {
        println("$this 这是Tee()方法")
    }
}

fun Int.intT() {
    println(this)
}

fun Te.teeTest() {
    println(this)
}

class StuTest {
    var p = 1

    fun test() {
        "StuTest".comp(100)
    }

    fun String.comp(int: Int) {
        println("$this@StuTest : $this : $int")
    }
}

fun main() {
    val isEmptyStringList: List<String>.() -> Boolean = List<String>::isEmpty
    val tp = StuTest::p
    StuTest().apply {
        tp.set(this, 2)
        println(tp.get(this))
        "s".comp(1)
    }
    val a = ::test
    a.invoke(188)
    val c : ()->Unit = Te::teeTest
    c.invoke()
    val d = Te::Tee
    d.invoke()
    val e = Int::intT
    e.invoke(2)
    // b 是函数类型的引用，调用的时候，需要传入接收者
    val b: Int.(String) -> Unit = {
        println(this)
        println(it)
    }
    b.invoke(1, "这是一个lambada函数")
    b.myTest("这是一个lambada函数", 10)
}