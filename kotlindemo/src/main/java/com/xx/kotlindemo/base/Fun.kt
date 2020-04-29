package com.xx.kotlindemo.base

class Fun{

    /**
     * 没有返回值的方法形式
     */
    fun unitFun1() {
        println("这是没有返回值的方法-1")
    }

    fun unitFun2(): Unit {
        println("这是没有返回值的方法-2")
        return Unit
    }

    fun unitFun3(): Unit {
        println("这是没有返回值的方法-3")
        return
    }


    /**
     * 有返回值的方法形式
     */
    fun intFun(): Int {
        println("这是返回值是 Int 的方法")
        return 2
    }

    fun longFun(): Long {
        println("这是返回值是 Long 的方法")
        return 128L
    }

    fun doubleFun(): Double {
        println("这是返回值是 Double 的方法")
        return 2.2
    }


    /**
     * 有默认参数的方法形式
     *
     * 注意：
     *      当具有默认参数时，可以对是参数有默认值的参数不传递参数值。
     *      当该函数是一个成员函数时,并且该函数时覆写继承类中的方法时，则该成员函数必须从签名中省略该函数的默认值。其实这一点不必过于看重，因为在你覆写方法时，编辑器都默认会帮你实现的
     */
    fun defaultParamFun(numA: Int = 1, numB: Float = 1.2f, numC: Boolean = false) {
        println("numA --> $numA,\t numB --> $numB,\t numC --> $numC")
    }


    /**
     * 可变参数 ---> 关键字 vararg
     *
     * 注意：
     *      在传递参数值时，我们可以一个一个参数的传递，或者可以直接传递一个当前定义类型的数组。不过在传递数组时，请使用伸展操作符( * )。
     */
    fun variableIntParamsFun(vararg params: Int = intArrayOf(1, 2, 3, 4, 5)) {
        for (i in params) {
            print("i --> $i \t")
        }
        println()
    }

    fun variableStringParamsFun(vararg params: String = arrayOf("Hello")) {
        for (s in params.withIndex()) {
            println("s --> $s,\t s.value --> ${s.value},\t s.index --> ${s.index}")
        }
    }

    fun testInnerFun() {
        fun innerFun() {
            println("inner fun")
        }

        innerFun()
    }

    val sample: Int.(Int) -> ((Int) -> Unit) = {
        ab -> {
//            a -> println("this = $this, ab = $ab, a = $a, this + ab = ${this.plus(ab)}, ab + a = ${ab.plus(a)}, this + a = ${this.plus(a)}")
            a -> run {
                println("this = $this, ab = $ab, a = $a, this + ab = ${this.plus(ab)}, ab + a = ${ab.plus(a)}, this + a = ${this.plus(a)}")
            }
            println("ab + 3 = ${ab + 3}")
        }
    }

    val onClick:() -> Int = {
        println("This is onClick fun!")
        1
    }

    val sum: /*Int.(Double) -> Int =*/(Int, Int) -> Int = {
        abs, bbs -> abs.plus(bbs)
    }

    val sample2: Int.(Int, Int) -> Int = {
        value, _ -> this.plus(value)
    }

    val sum1 = fun Int.(other: Int) : Int = this.plus(other)

    val a
        get() = 2

}

fun Fun.add(i:Int): Int {
    return this.a.plus(i)
}

fun main() {
    val mFun = Fun()

//    mFun.defaultParamFun(1, 3f, true)
//    mFun.defaultParamFun(numA = 1, numB = 3f, numC = true)
//    mFun.defaultParamFun(numB = 3f, numC = true)
    val intArr = intArrayOf(6, 7, 8, 9)
    mFun.variableIntParamsFun(*intArr)
    mFun.variableStringParamsFun()
    mFun.testInnerFun()
    println(mFun.onClick.invoke())
    println(mFun.sum1(5, 6))
    mFun.sample(1, 2)(3)

//    mFun.add(1)

//    println("mFun.a --> ${mFun.a}")
//    mFun.a = 3
//    println("mFun.a --> ${mFun.a}")
}