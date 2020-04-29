package com.xx.kotlindemo.study

import kotlin.properties.Delegates

/**
 * 委托属性
 *
 * 一般有这些情况需要用到委托属性
 *
 * 延迟属性（lazy properties）: 其值只在首次访问时计算； Study-3 有示例
 *
 * 可观察属性（observable properties）: 监听器会收到有关此属性变更的通知；
 *
 * 把多个属性储存在一个映射（map）中，而不是每个存在单独的字段中。
 */
class WTUser {
    // 可观察属性
    var name: String by Delegates.observable("no name") {
        /**
         * Delegates.observable() 接受两个参数：初始值与修改时处理程序（handler）。
         *
         * 每当我们给属性赋值时会调用该处理程序（在赋值后执行）。它有三个参数：被赋值的属性、旧值与新值
         */
        prop, old, new ->
        println("$old -> $new")
        // 不需要赋值
    }

    var age: Int by Delegates.vetoable(0) {
        /**
         * 如果你想能够截获一个赋值并“否决”它，就使用 vetoable() 取代 observable()。
         *
         * 在属性被赋新值生效之前会调用传递给 vetoable 的处理程序。
         */
        prop, old, new ->
        new > old
        // 不需要赋值
    }
}

fun main() {
    val user = WTUser()
    println(user.name)
    println(user.age)

    user.name = "John"
    user.age = -1
    println(user.name)
    println(user.age)
}

/**
 * 委托属性的要求
 *
 *  对于一个只读属性（即 val 声明的），委托必须提供一个名为 getValue 的函数，该函数接受以下参数：
 *
 *  thisRef —— 必须与 属性所有者 类型（对于扩展属性——指被扩展的类型）相同或者是它的超类型；
 *  property —— 必须是类型 KProperty<*> 或其超类型。
 *
 *  这个函数必须返回与属性相同的类型（或其子类型）。
 *
 *
 *  对于一个可变属性（即 var 声明的），委托必须额外提供一个名为 setValue 的函数，该函数接受以下参数：
 *
 *  thisRef —— 同 getValue()；
 *  property —— 同 getValue()；
 *  new value —— 必须与属性同类型或者是它的子类型。
 *
 *
 *  getValue() 或/与 setValue() 函数可以通过委托类的成员函数提供或者由扩展函数提供。
 *
 *  当你需要委托属性到原本未提供的这些函数的对象时后者会更便利。
 *
 *  两函数都需要用 operator 关键字来进行标。
 *
 *  委托类可以实现包含所需 operator 方法的 ReadOnlyProperty 或 ReadWriteProperty 接口之一。
 *
 *  这俩接口是在 Kotlin 标准库中声明的
 */