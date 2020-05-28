package com.xx.androiddemo.span

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.CharacterStyle
import androidx.appcompat.app.AppCompatActivity
import com.xx.androiddemo.R
import kotlinx.android.synthetic.main.activity_fake_span.*

class FakeBoldSpanActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fake_span)

        textView2.typeface = Typeface.DEFAULT_BOLD

        val spannable = SpannableString(textView3.text)
        spannable.setSpan(FakeBoldSpan(), 0, textView3.text.length, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
        textView3.text = spannable
    }
}

class FakeBoldSpan: CharacterStyle() {
    override fun updateDrawState(tp: TextPaint?) {
        tp?.style = Paint.Style.FILL_AND_STROKE
        tp?.strokeWidth = 1f
    }
}