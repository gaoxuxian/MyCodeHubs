package com.xx.androiddemo.canvas

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.xx.androiddemo.R
import com.xx.commonlib.PxUtil

class Canvas1Activity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PxUtil.init(this)
        val parent = FrameLayout(this)
        setContentView(parent)

        var canvas1View = Canvas1View(this)
        var params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, PxUtil.sV_1080p(300))
        parent.addView(canvas1View, params)

        canvas1View = Canvas1View(this, true)
        params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, PxUtil.sV_1080p(300))
        params.topMargin = PxUtil.sV_1080p(400)
        parent.addView(canvas1View, params)
    }
}

class Canvas1View(context: Context, private val drawClip: Boolean = false): View(context) {
    constructor(context: Context) : this(context, drawClip = false)
    private var bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
    private var bitmap_matrix: Matrix = Matrix()
    private var paint: Paint = Paint()

    override fun onDraw(canvas: Canvas?) {
        for (i in 0..10) {
            canvas?.save()
            val left = 40 * i
            val right = left + 60

            if (drawClip && i != 10) {
                canvas?.clipRect(left, 0, right, measuredHeight)
            }
            drawBitmap(canvas, left.toFloat())
            canvas?.restore()
        }
    }

    private fun drawBitmap(canvas: Canvas?, left:Float) {
        bitmap_matrix.reset()
        bitmap_matrix.setTranslate(left, 0f)
        canvas?.drawBitmap(bitmap, bitmap_matrix, paint)
    }
}