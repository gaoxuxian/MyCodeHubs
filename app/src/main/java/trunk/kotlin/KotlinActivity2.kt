package trunk.kotlin

import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerActivity : Activity() {

    private var mRecyclerView : RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = FrameLayout(this)
        var lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        layout.layoutParams = lp
        setContentView(layout)

        var data = ArrayList<String>()
        for (i in 1..50) {
            data.add(i.toString())
        }

        mRecyclerView = RecyclerView(this)
        mRecyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerView?.adapter = MyAdapter(data = data)
        mRecyclerView?.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.set(5, 0, 5, 0)
            }
        })
        lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 400)
        lp.gravity = Gravity.CENTER
        layout.addView(mRecyclerView, lp)

        var button = Button(this)
        button.text = "test left"
        button.setOnClickListener {
            val view = mRecyclerView?.getChildAt(1)
            val animator = ValueAnimator.ofInt(500, 10)
            var dx = 500
            animator.addUpdateListener { v ->
                val value = v.animatedValue as Int
                val x = dx - value
                dx = value
                if (view != null) {
                    view.layoutParams.width = value
                }
                mRecyclerView?.scrollBy(-x, 0)
                mRecyclerView?.requestLayout()
            }
            animator.duration = 2000
            animator.start()
//            mRecyclerView?.offsetChildrenHorizontal(240)
//            mRecyclerView?.postDelayed({
//                mRecyclerView?.requestLayout()
//            }, 1000)
        }
        lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        layout.addView(button, lp)

        button = Button(this)
        button.text = "test right"
        button.setOnClickListener {
            val view = mRecyclerView?.getChildAt(1)
            val animator = ValueAnimator.ofInt(500, 10)
            animator.addUpdateListener {
                val value = it.animatedValue as Int
                if (view != null) {
                    view.layoutParams.width = value
                }
                mRecyclerView?.requestLayout()
            }
            animator.duration = 2000
            animator.start()
        }
        lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.CENTER_HORIZONTAL
        layout.addView(button, lp)

        val button1 = Button(this)
        button1.text = "test animator"
        button1.setOnClickListener {
            data[0] = (0).toString()
            mRecyclerView?.adapter?.notifyItemChanged(0)
        }
        lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        lp.gravity = Gravity.END
        layout.addView(button1, lp)
    }
}

internal class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

private class MyAdapter(private var data : ArrayList<String>) : RecyclerView.Adapter<MyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = TextView(parent.context)
        view.setTextColor(Color.WHITE)
        view.minWidth = 500
        view.gravity = Gravity.CENTER
        view.setBackgroundColor(ColorUtils.setAlphaComponent(Color.RED, (255 * 0.6f).toInt()))
        val rp = RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.MATCH_PARENT)
        view.layoutParams = rp
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return 50
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val tv = holder.itemView as TextView
        tv.text = getData(position)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains("")) {
            val tv = holder.itemView as TextView
            tv.text = getData(position)
            return
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    private fun getData(index : Int) : String? {
        return data[index]
    }
}
