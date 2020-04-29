package com.xx.kotlindemo.study

/**
 * 类型别名: typealias
 *
 * 类型别名为现有类型提供替代名称。 如果类型名称太长，你可以另外引入较短的名称，并使用新的名称替代原类型名
 */
typealias Predicate<T> = (T) -> Boolean

fun foo(p: Predicate<Int>) = p(42)

typealias Username = String
typealias Password = Int

fun login(s: Username, t: Password) {
    println("登陆的账号是: $s\n登陆的密码是: $t")
}

fun login1(s: String, t: Int) {
    println("登陆的账号是: $s\n登陆的密码是: $t")
}

fun main() {

    /**
     * 类型别名不会引入新类型。 它们等效于相应的底层类型。
     *
     * 当你在代码中添加 typealias Predicate<T> 并使用 Predicate<Int> 时，Kotlin 编译器总是把它扩展为 (Int) -> Boolean。
     *
     * 因此，当你需要泛型函数类型时，你可以传递该类型的变量
     */

    val f: (Int) -> Boolean = { it > 0 }
    println(foo(f)) // 输出 "true"

    val p: Predicate<Int> = { it > 0 }
    println(listOf(1, -2).filter(p)) // 输出 "[1]"

    login("小明", 123456)
    login1("小明", 123456)
}