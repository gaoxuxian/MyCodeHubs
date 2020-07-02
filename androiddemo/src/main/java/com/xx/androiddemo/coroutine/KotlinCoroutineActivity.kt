package com.xx.androiddemo.coroutine

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import com.xx.androiddemo.R
import kotlinx.android.synthetic.main.activity_kotlin_coroutine.*
import kotlinx.coroutines.*
import kotlin.coroutines.suspendCoroutine

class KotlinCoroutineActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_coroutine)

        kotlin_coroutine_with_context.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                Log.e("xxx","输出这个Continuation == ${this.coroutineContext}")

//                getUser()
//
//                Log.e("xxx", "launch thread is ${Thread.currentThread().name}")
//
                withContext(Dispatchers.Default) {
                    Log.e("xxx", "withContext thread is ${Thread.currentThread().name}")
                }

                val start = System.currentTimeMillis()
//                val async1 = async(Dispatchers.IO) {
//                    Thread.sleep(3000)
//                    1
//                }
//
//                val async2 = async(Dispatchers.IO) {
//                    Thread.sleep(5000)
//                    2
//                }
                delay(90000)
//                val result = async1.await() + async2.await()
                val resultTime = System.currentTimeMillis() - start
//
                Log.e("xxx", "耗时：${resultTime}, thread is ${Thread.currentThread().name}")
                Log.e("xxx", "onCreate: 协程执行完毕")
            }

//            runBlocking(Dispatchers.IO) {
//                Log.e("xxx", "runblocking 执行完毕, thread is ${Thread.currentThread().name}")
//            }
        }

//        MyTest().apply {
//            test(2)
//        }
//
//        with(MyTest()) {
//            test(1)
//        }
    }

    private suspend fun getUser() = suspendCoroutine<String> {
        Log.e("xxx", "getUser: this is test, it == ${it.context}")
    }
}

class MyTest {

    fun Activity.test(int: Int) {
        println("这是 MyTest 类的 Activity 扩展方法, Activity = ${this}, value = ${int}")
    }

    fun getF() {

    }
}