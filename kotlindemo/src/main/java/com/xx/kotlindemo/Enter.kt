package com.xx.kotlindemo

import kotlinx.coroutines.*

suspend fun ioDelay() {
    withContext(Dispatchers.IO) {
        System.out.println("ioDelay start, Thread-name: " + Thread.currentThread().name)
        delay(1000)
        System.out.println("ioDelay end, Thread-name: " + Thread.currentThread().name)
        mainDelay()
    }
}

suspend fun mainDelay() {
    withContext(Dispatchers.Default) {
        System.out.println("mainDelay start, Thread-name: " + Thread.currentThread().name)
        delay(15000)
        System.out.println("mainDelay end, Thread-name: " + Thread.currentThread().name)
    }
}

fun main() {
    System.out.println("start, Thread-name: " + Thread.currentThread().name)
   GlobalScope.launch {
       ioDelay()
       System.out.println("delay start, Thread-name: " + Thread.currentThread().name + ", 时间: " + System.currentTimeMillis())
       delay(10000)
       System.out.println("delay end, Thread-name: " + Thread.currentThread().name + ", 时间: " + System.currentTimeMillis())
   }
    System.out.println("end, Thread-name: " + Thread.currentThread().name)
}