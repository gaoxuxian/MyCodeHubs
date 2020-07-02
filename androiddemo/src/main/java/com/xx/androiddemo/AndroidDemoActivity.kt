package com.xx.androiddemo

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
<<<<<<< HEAD
import com.xx.androiddemo.coroutine.KotlinCoroutineActivity
=======
import androidx.fragment.app.DialogFragment
>>>>>>> 616294280606704ce639dace0dbf2e5a6a19c27d
import com.xx.androiddemo.inke.InKeGiftDemoActivity
import kotlinx.android.synthetic.main.activity_android_demo.*

class AndroidDemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_android_demo)
<<<<<<< HEAD
        //        startActivity(Intent(this as Context, ServiceActivity::class.java))
        //        startActivity(Intent(this as Context, BroadcastActivity::class.java))
        //        startActivity(Intent(this as Context, Animation1Activity::class.java))
        //        startActivity(Intent(this as Context, InKeGiftDemoActivity::class.java))
        //        startActivity(Intent(this as Context, DoubleViewPagerActivity::class.java))
        //        startActivity(Intent(this as Context, DoubleViewPager2Activity::class.java))
        startActivity(Intent(this as Context, KotlinCoroutineActivity::class.java))
=======
//        startActivity(Intent(this as Context, ServiceActivity::class.java))
//        startActivity(Intent(this as Context, BroadcastActivity::class.java))
//        startActivity(Intent(this as Context, Animation1Activity::class.java))
//        startActivity(Intent(this as Context, InKeGiftDemoActivity::class.java))
//        startActivity(Intent(this as Context, DoubleViewPagerActivity::class.java))
//        startActivity(Intent(this as Context, DoubleViewPager2Activity::class.java))
//        finish()
>>>>>>> 616294280606704ce639dace0dbf2e5a6a19c27d
    }

    override fun onResume() {
        super.onResume()

        MyDialog().show(supportFragmentManager, "1")
    }

}


class MyDialog: DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("xxx", "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e("xxx", "onCreateView")
        return inflater.inflate(R.layout.test_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e("xxx", "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.e("xxx", "onActivityCreated")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Log.e("xxx", "onCreateDialog")
        return super.onCreateDialog(savedInstanceState)
    }
}