package com.xx.androiddemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xx.androiddemo.coroutine.KotlinCoroutineActivity
import com.xx.androiddemo.inke.InKeGiftDemoActivity
import kotlinx.android.synthetic.main.activity_android_demo.*

class AndroidDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_android_demo)
        //        startActivity(Intent(this as Context, ServiceActivity::class.java))
        //        startActivity(Intent(this as Context, BroadcastActivity::class.java))
        //        startActivity(Intent(this as Context, Animation1Activity::class.java))
        //        startActivity(Intent(this as Context, InKeGiftDemoActivity::class.java))
        //        startActivity(Intent(this as Context, DoubleViewPagerActivity::class.java))
        //        startActivity(Intent(this as Context, DoubleViewPager2Activity::class.java))
        startActivity(Intent(this as Context, KotlinCoroutineActivity::class.java))
    }

}
