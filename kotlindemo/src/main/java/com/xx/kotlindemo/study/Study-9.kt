package com.xx.kotlindemo.study

class Study9 {

    /**
     * 数值比较 (与Java有区别)
     *
     * 1、比较两个值是否相等 (==)
     *
     * 2、比较两个值的内存地址是否相等 (===)
     */
    private var a: Int = 127
    private var b: Int? = a
    private var c: Int? = 127
    private var d: Int? = 127

    private var e: Int = 128
    private var f: Int? = e
    private var g: Int? = 128
    private var h: Int? = 128

    private var i: Double = 200.002
    private var j: Double? = i
    private var k: Double? = 200.002
    private var l: Double? = 200.002

    fun printDataEqual() {
        println("================ Int 常量池【内】的比较 ===============")
        println("a: Int = $a, b: Int? = a => $b, c: Int? = $c, d: Int? = $d")

        println("a == b => ${a == b}, a === b => ${a === b}")

        println("c == d => ${c == d}, c === d => ${c === d}")

        println("a == c => ${a == c}, a === c => ${a === c}")

        println("b == c => ${b == c}, b === c => ${b === c}")

        println("================ Int 常量池【外】的比较 ===============")
        println("e: Int = $e, f: Int? = e => $f, g: Int? = $g, h: Int? = $h")

        println("e == f => ${e == f}, e === f => ${e === f}")

        println("g == h => ${g == h}, g === h => ${g === h}")

        println("e == g => ${e == g}, e === g => ${e === g}")

        println("f == g => ${f == g}, f === g => ${f === g}")

        println("================ Double 的比较 ===============")
        println("i: Double = $i, j: Double? = i => $j, k: Double? = $k, l: Double? = $l")

        println("i == j => ${i == j}, i === j => ${i === j}")

        println("k == l => ${k == l}, k === l => ${k === l}")

        println("i == k => ${i == k}, i === k => ${i === k}")

        println("j == k => ${j == k}, j === k => ${j === k}")
    }
}

fun main() {
    val study = Study9()
    study.printDataEqual()
}


/*
    Kotlin 基础数据类型踩坑

// <<<=====>>> 产生装箱 <<<=====>>>
var a: Int = 128
var b: Int? = a
var f: Int? = a

// <<<=====>>> 不产生装箱(个人猜测)，并且共用内存地址 <<<=====>>> 【只有基础数据类型有这个区别】
var c: Int? = 128
var d: Int? = 128
var e: Int? = 128

等同在 Android 平台用 Java 代码实现的情况
int a = 128;
Integer b = new Integer(128);
Integer f = new Integer(128);

Integer c = new Integer(128);
Integer d = c;
Integer e = c;

用 Kotlin 的比较
∴ a == b => true, a === b => false

∴ b == f => true, b === f => false

∴ c == d => true, c === d => true

∴ d == e => true, d === e => true


等同在 Java 平台用 Java 代码实现的情况
int a = 128;
Integer b = new Integer(128);
Integer f = new Integer(128);
Integer c = new Integer(128);
Integer d = new Integer(128);
Integer e = new Integer(128);

用 Kotlin 的比较
∴ a == b => true, a === b => false

∴ b == f => true, b === f => false

∴ c == d => true, c === d => false // 与 Android 环境有区别

∴ d == e => true, d === e => false // 与 Android 环境有区别


结论：如果产生装箱，那么每次都是 new 的对象，内存地址自然不相同，如果不产生装箱，则复用同一个对象，内存地址相同

 */