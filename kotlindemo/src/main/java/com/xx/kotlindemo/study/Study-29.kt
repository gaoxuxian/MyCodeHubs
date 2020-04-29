package com.xx.kotlindemo.study

/**
 * 泛型 part three
 *
 * 星投影 ---> 有时你想说，你对类型参数一无所知，但仍然希望以安全的方式使用它。 这里的安全方式是定义泛型类型的这种投影，该泛型类型的每个具体实例化将是该投影的子类型。
 */
class Generic3 {
    fun foo(t: Array<*>) {

        /**
         * 对于 Foo <out T : TUpper>，其中 T 是一个具有上界 TUpper 的协变类型参数，Foo <*> 等价于 Foo <out TUpper>。 这意味着当 T 未知时，你可以安全地从 Foo <*> 读取 TUpper 的值。
         *
         * 对于 Foo <in T>，其中 T 是一个逆变类型参数，Foo <*> 等价于 Foo <in Nothing>。 这意味着当 T 未知时，没有什么可以以安全的方式写入 Foo <*>。
         *
         * 对于 Foo <T : TUpper>，其中 T 是一个具有上界 TUpper 的不型变类型参数，Foo<*> 对于读取值时等价于 Foo<out TUpper> 而对于写值时等价于 Foo<in Nothing>。
         *
         * 如果泛型类型具有多个类型参数，则每个类型参数都可以单独投影。
         *
         * 例如，如果类型被声明为 interface Function <in T, out U>，我们可以想象以下星投影：
         *
         * Function<*, String> 表示 Function<in Nothing, String>；
         *
         * Function<Int, *> 表示 Function<Int, out Any?>；
         *
         * Function<*, *> 表示 Function<in Nothing, out Any?>。
         */

        // 读: t[] --> Any?
        // 写: t.set(index: Index, value: Nothing)
    }
}

fun main() {
    val generic = Generic3()

    generic.foo(arrayOf(1, "23", null)) // 原型:Array<T> , 所以Array<*> 相当于 Array<Any?>
}