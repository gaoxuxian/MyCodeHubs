package com.xx.kotlindemo.study

/**
 * 扩展 part three
 *
 * 扩展属性
 */
class Expand3 {
    var x: Int = 1

    companion object Test {
        fun foo():String {
            return "Expand3.Test"
        }
    }
}

/**
 * 注意：由于扩展没有实际的将成员插入类中，因此对扩展属性来说幕后字段是无效的。
 *
 * 这就是为什么扩展属性不能有初始化器。他们的行为只能由显式提供的 getters/setters 定义。
 */

// 相当于只是定义了额外的 getter\setter 方法
var Expand3.size
    get() = this.x
    set(value) {
        this.x = value
    }

// 相当于只是一个getter方法
val Expand3.name
    get() = "Expand3" + "+${this.x}"

// 伴生对象的扩展
fun Expand3.Test.printFoo() {
    println(this.foo())
}

fun main() {
    val expand = Expand3()

    println(expand.x)
    expand.size = 3
    println(expand.x)

    println(expand.name)

    Expand3.printFoo()
}