package com.xx.kotlindemo.study

/**
 * 密封类
 *
 * 密封类用来表示受限的类继承结构：当一个值为有限集中的类型、而不能有任何其他类型时。
 *
 * 在某种意义上，他们是枚举类的扩展：
 *
 * 枚举类型的值集合也是受限的，但每个枚举常量只存在一个实例，而密封类的一个子类可以有可包含状态的多个实例。
 *
 * 要声明一个密封类，需要在类名前面添加 sealed 修饰符。
 *
 * 虽然密封类也可以有子类，但是所有子类都必须在与密封类自身相同的文件中声明
 */
sealed class Expr {
    /**
     * 一个密封类是自身抽象的，它不能直接实例化并可以有抽象（abstract）成员。
     *
     * 密封类不允许有非-private 构造函数（其构造函数默认为 private）。
     *
     * 请注意，扩展密封类子类的类（间接继承者）可以放在任何位置，而无需在同一个文件中。
     */
}
data class Const(val number: Double) : Expr()
data class Sum(val e1: Expr, val e2: Expr) : Expr()
object NotANumber : Expr()

/**
 * 使用密封类的关键好处在于使用 when 表达式 的时候，如果能够验证语句覆盖了所有情况，就不需要为该语句再添加一个 else 子句了。
 *
 * 当然，这只有当你用 when 作为表达式（使用结果）而不是作为语句时才有用。
 */
fun eval(expr: Expr): Double = when(expr) {
    is Const -> expr.number
    is Sum -> eval(expr.e1) + eval(expr.e2)
    NotANumber -> Double.NaN
    // 不再需要 `else` 子句，因为我们已经覆盖了所有的情况
}

fun main() {
    println(eval(Sum(e1 = Const(10.0), e2 = Const(20.0))))
}