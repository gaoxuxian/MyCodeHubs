package com.xx.kotlindemo.study

/**
 * 数据类
 *
 * 为了确保生成的代码的一致性以及有意义的行为，数据类必须满足以下要求：
 *
 * 主构造函数需要至少有一个参数
 *
 * 主构造函数的所有参数需要标记为 val 或 var
 *
 * 数据类不能是抽象、开放、密封或者内部的；
 */
data class User(val name: String = "", var age: Int = 0) {

}

data class Person(val name: String) {

    /**
     * 请注意，对于那些自动生成的函数，编译器只使用在主构造函数内部定义的属性。
     *
     * 如需在生成的实现中排出一个属性，请将其声明在类体中
     *
     * 在自动生成的函数 toString()、 equals()、 hashCode() 以及 copy() 的实现中只会用到 name 属性，并且只有一个 component 函数 component1()。
     *
     * 虽然两个 Person 对象可以有不同的年龄，但它们会视为相等。{@Link main()}
     */

    var age: Int = 0
}

fun main() {
    val person1 = Person("John")
    val person2 = Person("John")
    person1.age = 10
    person2.age = 20

    println("person1 == person2: ${person1 == person2}")
    println("person1 with age ${person1.age}: ${person1}")
    println("person2 with age ${person2.age}: ${person2}")

    // 在很多情况下，我们需要复制一个对象改变它的一些属性，但其余部分保持不变。
    val jack = User(name = "Jack", age = 1)
    val olderJack = jack.copy(age = 2)
}