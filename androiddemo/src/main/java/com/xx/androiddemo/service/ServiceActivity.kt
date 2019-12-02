package com.xx.androiddemo.service

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.xx.androiddemo.R
import kotlinx.android.synthetic.main.activity_service.*

class ServiceActivity : AppCompatActivity(), ServiceConnection {

    private val service = "com.xx.androiddemo.service.ServiceTest"
    private val tag = this.javaClass.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)

        start_service.setOnClickListener {
            v: View? ->
            startService(Intent(this, Class.forName(service)))
            Log.d(tag, "start service end")
        }

        stop_service.setOnClickListener {
            _: View? ->
            stopService(Intent(this, Class.forName(service)))
        }

        /**
         * bind service 是一个异步操作，如果是同步，应该是 end 先打印，onServiceConnected 后打印
         */
        bind_service.setOnClickListener {
            v ->
            bindService(Intent(this, Class.forName(service)), this, BIND_AUTO_CREATE)
            Log.d(tag, "bind service end")
        }

        unbind_service.setOnClickListener {
            unbindService(this)
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        Log.d(tag, "onServiceDisconnected")
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        (service as? ServiceTest.BinderTest)?.showServiceName()
        Log.d(tag, "onServiceConnected")
    }

}
