package com.xx.androiddemo.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.xx.androiddemo.IClass
import com.xx.androiddemo.R
import com.xx.androiddemo.Student
import kotlinx.android.synthetic.main.activity_service.*
import java.lang.NullPointerException

class ServiceActivity : AppCompatActivity() {

    private val localConnection: LocalServiceConnection by lazy { LocalServiceConnection() }
    private val processConnection: ProcessServiceConnection by lazy { ProcessServiceConnection() }
    private val tag = this.javaClass.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)

        start_process_service.setOnClickListener {
            startService(Intent(this, ProcessService::class.java))
        }

        stop_process_service.setOnClickListener {
            stopService(Intent(this, ProcessService::class.java))
        }

        bind_process_service.setOnClickListener {
            bindService(Intent(this, ProcessService::class.java), processConnection, Context.BIND_AUTO_CREATE)
        }

        unbind_process_service.setOnClickListener {
            unbindService(processConnection)
        }

        start_local_service.setOnClickListener {
            v: View? ->
            startService(Intent(this, LocalService::class.java))
        }

        stop_local_service.setOnClickListener {
            _: View? ->
            stopService(Intent(this, LocalService::class.java))
        }

        /**
         * bind service 是一个异步操作，如果是同步，应该是 end 先打印，onServiceConnected 后打印
         */
        bind_local_service.setOnClickListener {
            v ->
            bindService(Intent(this, LocalService::class.java), localConnection, BIND_AUTO_CREATE)
        }

        unbind_local_service.setOnClickListener {
            unbindService(localConnection)
        }
    }

    private inner class LocalServiceConnection: ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(tag, "onServiceDisconnected")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            (service as? LocalService.IConnectService)?.showServiceName()
            Log.d(tag, "onServiceConnected")
        }
    }

    private inner class ProcessServiceConnection: ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(tag, "onServiceDisconnected")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = IClass.Stub.asInterface(service)
            binder.addStudent(Student("小红", 20))
            val student = binder.findStudent("小红")
            Log.d(tag, "onServiceConnected, student.name == ${student.name}")
        }
    }
}
