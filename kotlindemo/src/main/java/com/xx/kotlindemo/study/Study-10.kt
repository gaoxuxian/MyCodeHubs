package com.xx.kotlindemo.study

// 1、顶层声明
const val valConstString: String = "顶层常量string"

// 2、object 类声明 -- 单例
object TestConst{
    const val valConstTestString: String = "Object TestConst 的常量string"
}

/**
 * 常量 ---> const 关键字
 *
 * 定义：
 *      const val 变量名: 类型 = 确定的值
 *
 * 注意事项：
 *      1、可以在顶层声明
 *      2、可以在 object 修饰的类中声明，在 Kotlin 中称为 对象声明， 相当于 Java 中一种形式的单例类
 *      3、可以在伴生对象中声明
 */

class Study10 {

    // 3、伴生对象
    companion object {
        const val valConstVFirstInnerString: String = "VFirstInner 常量string"
    }
}