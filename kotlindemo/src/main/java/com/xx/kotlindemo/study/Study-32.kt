package com.xx.kotlindemo.study

/**
 * object 关键字
 *
 * 匿名内部类、伴生对象、
 */
open class Z(x: Int) {
    open val y: Int = x
}

interface V

class M {

    /**
     * 请注意，匿名对象可以用作只在本地和私有作用域中声明的类型。
     *
     * 如果你使用匿名对象作为公有函数的返回类型或者用作公有属性的类型，那么该函数或属性的实际类型会是匿名对象声明的超类型，如果你没有声明任何超类型，就会是 Any。在匿名对象中添加的成员将无法访问。
     */

    // 私有函数，所以其返回类型是匿名对象类型
    private fun foo() = object {
        val x: String = "x"
    }

    // 公有函数，所以其返回类型是 Any
    fun publicFoo() = object {
        val x: String = "x"
    }

    fun bar() {
        val x1 = foo().x        // 没问题
        //val x2 = publicFoo().x  // 错误：未能解析的引用“x”
    }
}

fun main() {
    // 匿名内部类
    val zv: Z = object : Z(1), V {
        /**
         * 如果超类型有一个构造函数，则必须传递适当的构造函数参数给它。 多个超类型可以由跟在冒号后面的逗号分隔的列表指定
         */
        override val y: Int
            get() = 10
    }
}