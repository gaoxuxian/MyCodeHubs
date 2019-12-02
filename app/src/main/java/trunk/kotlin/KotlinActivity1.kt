package trunk.kotlin

import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import trunk.R

class ViewOffsetLRActivity : Activity() {

    var mImageView : ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = FrameLayout(this)
        var lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        layout.layoutParams = lp
        setContentView(layout)

        mImageView = ImageView(this)
        mImageView?.setBackgroundColor(Color.RED)
        mImageView?.setImageResource(R.drawable.open_test_2)
        lp = FrameLayout.LayoutParams(400, 400)
        lp.gravity = Gravity.CENTER
        layout.addView(mImageView, lp)

        val button = Button(this)
        button.text = "test left"
        button.setOnClickListener {
            val animator = ValueAnimator.ofFloat(0f, 1f)
            animator.addUpdateListener {
                val value = it.animatedValue as Float
                mImageView?.left = (340 + 400 * value).toInt()
                mImageView?.scrollTo(0, 0)
                mImageView?.scrollBy((400 * value).toInt(), 0)
            }
            animator.addListener(
                onStart = {

                },

                onEnd = {
                    Log.e("***", "width = ${mImageView?.width}, height = ${mImageView?.height}")
                    val r = Rect()
                    mImageView?.getDrawingRect(r)
                    Log.e("***", "drawRect = $r")
                }
            )
            animator.duration = 2000
            animator.start()
        }
        lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        layout.addView(button, lp)

        val textView = TextView(this)
        textView.also {  }
        textView.addTextWatcher(
            afterTextChanged = {

            },
            beforeTextChanged = {
                s, start, count, after ->
                Log.e("", s.toString())
            }
        )
    }
}

inline fun TextView.addTextWatcher(
    crossinline afterTextChanged : (Editable?) -> Unit = {},
    crossinline beforeTextChanged : (CharSequence?, Int, Int, Int) -> Unit = {_, _, _, _ -> Unit},
    crossinline onTextChanged : (CharSequence?, Int, Int, Int) -> Unit = {_, _, _, _ -> Unit}) {
    val listener : TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            beforeTextChanged(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged(s, start, before, count)
        }

    }
    addTextChangedListener(listener)
}
