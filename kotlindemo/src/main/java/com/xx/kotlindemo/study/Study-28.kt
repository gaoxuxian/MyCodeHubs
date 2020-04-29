package com.xx.kotlindemo.study

/**
 * 泛型 part two
 *
 * 型变 --> 声明处型变 \ 类型投影
 *
 * 只能从中读取的对象为生产者，并称那些只能写入的对象为消费者
 *
 * PECS 代表生产者-Extens，消费者-Super（Producer-Extends, Consumer-Super）。
 */

/**
 * in\out 修饰符称为型变注解，并且由于它在类型参数声明处提供，所以我们称之为声明处型变
 */
interface Source<in T, out Y, U, R, V> {

    fun nextY(): Y // --->

    // fun nextY(y: Y): Y  ---> 编译报错，接口泛型声明时，Y 被声明为 只能被生产，不能被消费

    fun nextT(t: T)

    // fun nextT(t: T): T  ---> 编译报错，接口泛型声明时，T 被声明为 只能被消费，不能被生产

    fun nextU(u: U) : U  // ---> 可以，接口泛型声明时，U 并未被 in\out 修饰，既可以是函数的参数类型（消费），也可以是函数的返回值（生产）


    /**
     * 当一个已经声明了泛型的类型作为函数参数时，对类型的泛型用 in\out 修饰，称为 类型投影
     */
    fun fill(dest: Array<in R>) {
        // 若 R 是 String
        // Array<in R> 对应于 Java 的 Array<? super String>
        // 由于 in 修饰符的存在，Array<in R> 是可以被消费的，但 setter 方法只能接收 R 类型的
        // 另外，Array<in R> 也是可以调用 getter 方法的，但这不算是被生产，因为返回的是 Any? 类型
    }

    fun copy(from: Array<out V>) {
        // 若 V 是 Number
        // Array<in V> 对应于 Java 的 Array<? extend Number>
        // 由于 out 修饰符的存在，Array<out V> 只能被生产，也就是只能调用 getter 方法，返回 V 类型
    }
}

class SourceImpl: Source<Double, Float, Long, String, Number> {
    override fun nextY(): Float {
        return 1.0f
    }

    override fun nextT(t: Double) {
        println(t + 2.0)
    }

    override fun nextU(u: Long): Long {
        return u + 10_000L
    }

    override fun fill(dest: Array<in String>) {

    }

    override fun copy(from: Array<out Number>) {

    }

}

fun main() {
    val s = SourceImpl()

    s.fill(arrayOf('s', 1, 2.0))
    s.copy(arrayOf(1, 2.0, 100L, 3.0f))
}