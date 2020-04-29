package com.xx.kotlindemo.study

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * 类与继承 part two
 *
 * 继承与重写
 */
open class Base(p: Int) { // 从 Any 隐式继承
    open val x: Int = p
    open var y: Int = p * p

    open fun v() {
        println("This is Base v()")
    }

    fun nv() {
        println("This is Base nv()")
    }
}

class Derived(d: Int): Base(d) {

    override val x: Int = 3

    override var y: Int = 2
        get() = field // 幕后字段
        set(value) {
            field = value
        }

    /**
     * 如果派生类有一个主构造函数，其基类型可以（并且必须） 用基类的主构造函数参数就地初始化。
     */

    override fun v() {
//        super.v()
        println("This is Derived v()")
    }
}

class MyView: View {

    /**
     * 如果派生类没有主构造函数，那么每个次构造函数必须使用 super 关键字初始化其基类型，或委托给另一个构造函数做到这一点。
     *
     * 注意，在这种情况下，不同的次构造函数可以调用基类型的不同的构造函数：
     */

    constructor(ctx: Context): super(ctx)

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
}