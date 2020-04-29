package com.xx.kotlindemo.util

import android.util.Log
import android.util.LogPrinter

class MyLog {
    companion object {
        private const val DEBUG = Log.DEBUG
        private const val ERROR = Log.ERROR

        fun d(tag: String, msg: String) {
            LogPrinter(DEBUG, tag).println(msg)
        }

        fun e(tag: String, msg: String){
            LogPrinter(ERROR, tag).println(msg)
        }
    }
}