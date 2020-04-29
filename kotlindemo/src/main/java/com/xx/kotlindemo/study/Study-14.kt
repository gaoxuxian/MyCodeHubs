package com.xx.kotlindemo.study

/**
 * 类与继承 part one
 *
 * 类成员的初始化顺序
 */
open class Money constructor(unit: String) { // 主构造函数

    /**
     * 在实例初始化期间，初始化块按照它们出现在类体中的顺序执行，与属性初始化器交织在一起
     *
     * 请注意，主构造的参数可以在初始化块中使用。它们也可以在类体内声明的属性初始化器中使用
     */

    init { println("Initializing Money First init{}") }

    open val size: Int = unit.length.also { println("Initializing size in Money: $it") }

    init { println("Initializing Money Second init{}") }


}

class Dollar(unit: String):Money(unit.capitalize().also { println("Argument for Money: $it") }) {

    /**
     * 在构造派生类的新实例的过程中，第一步完成其基类的初始化（在之前只有对基类构造函数参数的求值），因此发生在派生类的初始化逻辑运行之前。
     */

    init { println("Initializing Dollar First init{}") }

    override val size: Int = (super.size + unit.length).also { println("Initializing size in Dollar: $it") }

    init { println("Initializing Dollar Second init{}") }

    /**
     * 1、如果类有一个主构造函数，每个次构造函数需要委托给主构造函数，可以直接委托或者通过别的次构造函数间接委托。
     *
     * 委托到同一个类的另一个构造函数用 this 关键字即可
     *
     *
     * 2、初始化块中的代码实际上会成为主构造函数的一部分。
     *
     * 委托给主构造函数会作为次构造函数的第一条语句，因此所有初始化块中的代码都会在次构造函数体之前执行。
     *
     * 即使该类没有主构造函数，这种委托仍会隐式发生，并且仍会执行初始化块
     *
     *
     * 3、如果一个非抽象类没有声明任何（主或次）构造函数，它会有一个生成的不带参数的主构造函数。
     *
     * 构造函数的可见性是 public。如果你不希望你的类有一个公有构造函数，你需要声明一个带有非默认可见性的空的主构造函数
     *
     * 注意：在 JVM 上，如果主构造函数的所有的参数都有默认值，编译器会生成 一个额外的无参构造函数，它将使用默认值。
     */
    constructor(unit: String, count: Int): this(unit) {// 次构造函数
        println("We have $count$unit")
    }
}

fun main() {
    Dollar("$", 100)
}