package com.xx.kotlindemo.study


/**
 * 内联类
 *
 * 内联类的成员也有一些限制：
 *
 *      内联类必须含有唯一的一个属性在主构造函数中初始化。
 *
 *      内联类不能含有 init 代码块
 *
 *      内联类不能含有幕后字段
 *
 *
 * 内联类允许去继承接口，但禁止内联类参与到类的继承关系结构中。这就意味着内联类不能继承其他的类而且必须是 final。
 */
inline class LoginUserName(private val name: String) {
    val value: String
        get() = name

    fun getter() = name
}

inline class LoginUserPassword(private val password: Int) {
    val value: Int
        get() = password

    fun getter() = password
}

fun login(s: LoginUserName, t: LoginUserPassword) {
    println("登陆的账号对象是: $s \t登陆的密码对象是: $t")
//    println("登陆的账号是: ${s.value} \t登陆的密码是: ${t.value}")
    println("登陆的账号是: ${s.getter()} \t登陆的密码是: ${t.getter()}")
}

fun main() {
    login(LoginUserName("小明"), LoginUserPassword(123456))
}

/*
    网友的总结：

    对基础类型进行包装，使传基础类型的参数时不容易写错。
    同时其在jvm中的表现还是基础类型，不会有性能问题。
 */