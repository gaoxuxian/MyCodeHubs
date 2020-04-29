package com.xx.kotlindemo.study

/**
 * 枚举
 */
enum class Color(val rgb: Int) {
    // 初始化
    RED(0xFF0000),GREEN(0X00FF00),BLUE(0X0000FF)
}

enum class ProtocolState {
    // 匿名内部类
    WAITING {
        override fun signal() = TALKING
    },

    TALKING {
        override fun signal() = WAITING
    };

    abstract fun signal(): ProtocolState
}

fun main() {
    println(Color.BLUE)
    println(Color.valueOf(Color.RED.toString()))
}