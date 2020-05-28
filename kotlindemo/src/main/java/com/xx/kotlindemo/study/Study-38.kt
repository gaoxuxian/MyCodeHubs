package com.xx.kotlindemo.study

import android.util.Log
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * 契约
 */

fun notNullTest(s:String?) {
    if (null != s) {
        Log.e("study-38", s.length.toString())
    }
}

fun notNullTest2(s:String?) {
    if (s.isNotNull()) {
        Log.e("study-38", s?.length.toString())
    }
}

fun String?.isNotNull(): Boolean {
    return this != null
}

@ExperimentalContracts
fun notNullTest3(s:String?) {
    if (s.isNotNull2()) {
        Log.e("study-38", s.length.toString())
    }
}

@ExperimentalContracts
fun String?.isNotNull2(): Boolean {
    contract {
        returns(true) implies (this@isNotNull2 != null)
    }
    return this != null
}