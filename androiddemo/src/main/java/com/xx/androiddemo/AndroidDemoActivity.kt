package com.xx.androiddemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.xx.androiddemo.activity.StandardActivity
import com.xx.androiddemo.anim.Animation1Activity
import com.xx.androiddemo.canvas.Canvas1Activity
import com.xx.androiddemo.service.ServiceActivity

import kotlinx.android.synthetic.main.activity_android_demo.*

class AndroidDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_android_demo)
        startActivity(Intent(this as Context, Animation1Activity::class.java))
        finish()
    }

}
