package com.xx.androiddemo.viewpager

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.xx.androiddemo.R
import kotlinx.android.synthetic.main.activity_double_viewpager.*

class DoubleViewPagerActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_double_viewpager)

//        out_viewpager.offscreenPageLimit = 5
        out_viewpager.adapter = OutAdapter()
    }

    inner class OutAdapter: PagerAdapter() {

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//            super.destroyItem(container, position, `object`)
            container.removeViewAt(position)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            when(position) {
                0 -> {
                    return ViewPager(container.context).apply {
                        offscreenPageLimit = 3
                        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                        adapter = InnerAdapter(Color.RED, Color.BLACK)
                        container.addView(this)
                    }
                }
                1 -> {
                    return ViewPager(container.context).apply {
                        offscreenPageLimit = 3
                        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                        adapter = InnerAdapter(Color.BLACK, Color.WHITE)
                        container.addView(this)
                    }
                }
                2 -> {
                    return TextView(container.context).apply {
                        setText("这是 Out_Adapter 第 ${position + 1} 个Item")
                        gravity = Gravity.CENTER
                        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                        container.addView(this)
                    }
                }
            }
            return super.instantiateItem(container, position)
        }

        override fun getCount(): Int {
            return 2
        }
    }

    inner class InnerAdapter(val color: Int, val textColor: Int): PagerAdapter() {

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            //            super.destroyItem(container, position, `object`)
            container.removeViewAt(position)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return TextView(container.context).also {
                it.setText("这是 Inner_Adapter 第 ${position + 1} 个Item")
                it.setTextColor(textColor)
                it.setBackgroundColor(color)
                it.gravity = Gravity.CENTER
                it.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                container.addView(it)
            }
        }

        override fun getCount(): Int {
            return 3
        }
    }
}