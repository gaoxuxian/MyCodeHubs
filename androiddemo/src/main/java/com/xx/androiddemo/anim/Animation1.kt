package com.xx.androiddemo.anim

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.xx.androiddemo.R
import com.xx.commonlib.PxUtil
import kotlinx.android.synthetic.main.activity_anim_1.*

class Animation1Activity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PxUtil.init(this)
        setContentView(R.layout.activity_anim_1)

        button1.setOnClickListener {
//            val animation = TranslateAnimation(Animation.ABSOLUTE, 0f, Animation.ABSOLUTE, 0f, Animation.ABSOLUTE, 0f, Animation.ABSOLUTE, 100f)
//            animation.duration = 2000
//            animation.fillAfter = true
//            animation.setAnimationListener(object :Animation.AnimationListener {
//                override fun onAnimationRepeat(animation: Animation?) {
//
//                }
//
//                override fun onAnimationEnd(animation: Animation?) {
//                    it.clearAnimation()
//                }
//
//                override fun onAnimationStart(animation: Animation?) {
//
//                }
//            })
//            button1.startAnimation(animation)

            val animation = ValueAnimator.ofFloat(0f, 100f)
            animation.duration = 2000
            animation.addUpdateListener {
                button1.translationY = it.animatedValue as Float
            }
            animation.start()
        }
    }
}