package com.xx.androiddemo.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

fun BroadcastReceiver.log(string: String?) {
    Log.e(this::class.simpleName, string)
}

class StaticReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val stringExtra = intent?.getStringExtra("msg")
        log(stringExtra)
    }
}

class DisorderlyReceiver1: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val stringExtra = intent?.getStringExtra("msg")
        log(stringExtra)
    }
}

class DisorderlyReceiver2: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val stringExtra = intent?.getStringExtra("msg")
        log(stringExtra)
    }
}

class OrderlyReceiver1: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val stringExtra = intent?.getStringExtra("msg")
        val resultExtras = getResultExtras(false)
        log(stringExtra + "ResultExtras == ${resultExtras?.getInt("extra")}")

        val bundle = Bundle()
        bundle.putInt("extra", 1)
        setResultExtras(bundle)
    }
}

class OrderlyReceiver2: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val stringExtra = intent?.getStringExtra("msg")
        val resultExtras = getResultExtras(false)
        log(stringExtra + "ResultExtras == ${resultExtras?.getInt("extra")}")

        val bundle = Bundle()
        bundle.putInt("extra", 2)
        setResultExtras(bundle)
    }
}

class OrderlyReceiver3: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val stringExtra = intent?.getStringExtra("msg")
        val resultExtras = getResultExtras(false)
        log(stringExtra + "ResultExtras == ${resultExtras?.getInt("extra")}")

        abortBroadcast()
    }
}