package com.xx.kotlindemo.study

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

open class ABC {

}

class Stu40A: ABC() {

}

class Stu40B: ABC() {

}

class Stu40C: ABC() {

}

private fun Stu40Test(list: ArrayList<in ABC>) {
    list.add(Stu40A())
    list.add(Stu40B())
    list.add(Stu40C())
}

private fun Stu40Test2(list: ArrayList<out ABC>) {
    list[0]
    return
}

fun main() {
//    runBlocking {
//        val flow: Flow<Int> = flow {
//            for (i in 1..3) {
//                delay(100) // 假装我们在这里做了一些有用的事情
//                emit(i) // 发送下一个值
//            }
//        }
//        flow.collect {
//            println("thread is ${Thread.currentThread().name}, value = ${it}")
//        }
//    }
    Stu40Test2(arrayListOf(ABC()))
    println("main")
}