package com.xx.kotlindemo.study

class Study3 {
    /**
     * 懒加载 ---> by 关键字 、lazy{} 高阶函数
     *
     * 通过使用懒加载，可以省去昂贵的属性初始化的成本直到它们真正需要。计算值然后保存并为了未来的任何时候的调用。
     *
     * lazy() 是接受一个 lambda 并返回一个 Lazy <T> 实例的函数，
     *
     * 返回的实例可以作为实现延迟属性的委托： 第一次调用 get() 会执行已传递给 lazy() 的 lambda 表达式并记录结果，
     *
     * 后续调用 get() 只是返回记录的结果。
     *
     * 定义：
     *      val 变量名: 类型 by lazy{}
     *
     * 注意事项：
     *      1、不能用类型推断的简写，变量类型后要用 by 连接 lazy{}
     *      2、只能是 val 变量
     */
    val value: String by lazy {
        println("Study3 value lazy init")
        "延迟初始化"
    }
}

fun main() {
    val study = Study3()
    println(study.value)
    println(study.value)
}