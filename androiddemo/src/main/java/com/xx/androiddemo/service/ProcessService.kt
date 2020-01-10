package com.xx.androiddemo.service

import android.app.Service
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.xx.androiddemo.IClass
import com.xx.androiddemo.Student

class ProcessService:Service() {

    private val tag = this.javaClass.name

    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "onCreate, 所在线程: " + Thread.currentThread().name)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(tag, "onStartCommand, 所在线程: " + Thread.currentThread().name)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(tag, "onBind, 所在线程: " + Thread.currentThread().name)
        return MyClassStudentManager()
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(tag, "onRebind, 所在线程: " + Thread.currentThread().name)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(tag, "onUnbind, 所在线程: " + Thread.currentThread().name)
        return super.onUnbind(intent)
    }

    override fun unbindService(conn: ServiceConnection) {
        super.unbindService(conn)
        Log.d(tag, "unbindService, 所在线程: " + Thread.currentThread().name)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy, 所在线程: " + Thread.currentThread().name)
    }

    inner class MyClassStudentManager: IClass.Stub() {
        private val studentList: HashMap<String?, Student?> by lazy { HashMap<String?,Student?>() }

        override fun findStudent(name: String?): Student {
            return studentList[name] as Student
        }

        override fun addStudent(student: Student?) {
            studentList[student?.name] = student
        }

    }
}