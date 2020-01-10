package com.xx.androiddemo.activity

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xx.androiddemo.R
import kotlinx.android.synthetic.main.activity_demo.*

fun getPackagePath(): String {
    return "com.xx.androiddemo.activity."
}

fun AppCompatActivity.log(tag: String, string: String) {
    Log.e(tag, this.javaClass.name + " " + string)
}

fun BaseActivity4Demo.startStandardActivity() {
    standard.setOnClickListener {
        v: View? ->
        startActivity(Intent(this, Class.forName(getPackagePath() + "StandardActivity")))
    }
}

fun BaseActivity4Demo.startStandardTaskActivity() {
    standardtask.setOnClickListener {
            v: View? ->
        startActivity(Intent(this, Class.forName(getPackagePath() + "StandardTaskActivity")))
    }
}

fun BaseActivity4Demo.startSingleTopActivity() {
    singletop.setOnClickListener {
        v ->
        startActivity(Intent(this, Class.forName(getPackagePath() + "SingleTopActivity")))
    }
}

fun BaseActivity4Demo.startSingleTaskActivity() {
    singletask.setOnClickListener {
        _->
        startActivity(Intent(this, Class.forName(getPackagePath() + "SingleTaskActivity")))
    }
}

fun BaseActivity4Demo.startSingleTaskOtherTaskActivity() {
    othertasksingletask.setOnClickListener {
        _: View?->
        startActivity(Intent(this, Class.forName(getPackagePath() + "SingleTaskOtherTaskActivity")))
    }
}

fun BaseActivity4Demo.startSingleInstanceActivity() {
    singleinstance.setOnClickListener {
        startActivity(Intent(this, Class.forName(getPackagePath() + "SingleInstanceActivity")))
    }
}

class StandardActivity : BaseActivity4Demo()

class StandardTaskActivity : BaseActivity4Demo()

class SingleTopActivity : BaseActivity4Demo()

class SingleTaskActivity : BaseActivity4Demo()

class SingleTaskOtherTaskActivity : BaseActivity4Demo()

class SingleInstanceActivity : BaseActivity4Demo()

/**
 * 利用adb 查看 activity 栈信息
 *
 * adb shell dumpsys activity activities
 */
open class BaseActivity4Demo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)
        this.startStandardActivity()
        this.startStandardTaskActivity()
        this.startSingleTopActivity()
        this.startSingleTaskActivity()
        this.startSingleTaskOtherTaskActivity()
        this.startSingleInstanceActivity()
        finish.setOnClickListener {
            finish()
        }
        log("***", "onCreate")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        log("***", "onNewIntent")
    }
}