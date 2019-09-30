package trunk.kotlin

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import trunk.R
import util.PxUtil

class NestedScrollActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PxUtil.init(this)

        val layout = MyLinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        val param = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        layout.layoutParams = param
        setContentView(layout)

        val imageView = ImageView(this)
        imageView.setImageResource(R.drawable.open_test_9)
        var param1 = LinearLayout.LayoutParams(PxUtil.sU_1080p(1080), (PxUtil.sU_1080p(1080) * 9f / 16f).toInt())
        layout.addView(imageView, param1)

        val data = ArrayList<String>()
        for (i in 1..100) {
            data.add(i.toString())
        }

        val recyclerView = RecyclerView(this)
        recyclerView.overScrollMode = View.OVER_SCROLL_NEVER
        recyclerView.isNestedScrollingEnabled = true
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = MyAdapter1(data = data)
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.set(5, 5, 5, 5)
            }
        })
        param1 = LinearLayout.LayoutParams(PxUtil.sU_1080p(1080), PxUtil.sV_1080p(1920))
//        param1 = LinearLayout.LayoutParams(PxUtil.sU_1080p(1080), PxUtil.sU_1080p(1920))
        layout.addView(recyclerView, param1)
    }
}

private class MyLinearLayout(context: Context) : LinearLayout(context), NestedScrollingParent3 {

    private val helper : NestedScrollingParentHelper by lazy { NestedScrollingParentHelper(this) }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return axes.and(ViewCompat.SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        helper.onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        val firstView = getChildAt(0)
        val scrollH = firstView.measuredHeight
        val hideTop = dy > 0 && scrollY < scrollH
        val child = (target as RecyclerView).getChildAt(0)
        val position = target.getChildAdapterPosition(child)
        val showTop = position == 0 && target.layoutManager?.getDecoratedTop(child) == 0 && dy < 0 && scrollY > 0

        if (hideTop) {
            val sDy = scrollH - scrollY
            if (dy <= sDy) {
                scrollBy(0, dy)
                consumed[1] = dy
            } else {
                scrollBy(0, sDy)
                consumed[1] = dy - sDy
            }
        } else if (showTop) {
            val sDy = -scrollY
            if (dy >= sDy) {
                scrollBy(0, dy)
                consumed[1] = dy
            } else {
                scrollBy(0, sDy)
                consumed[1] = dy - sDy
            }
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        val child = (target as RecyclerView).getChildAt(0)
        val position = target.getChildAdapterPosition(child)
        val showTop = position == 0 && target.layoutManager?.getDecoratedTop(child) == 0 && dyUnconsumed < 0 && scrollY > 0

        if (showTop) {
            val sDy = -scrollY
            if (dyUnconsumed >= sDy) {
                scrollBy(0, dyUnconsumed)
                consumed[1] = dyUnconsumed
            } else {
                scrollBy(0, sDy)
                consumed[1] = dyUnconsumed - sDy
            }
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {}

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {}

    override fun onStopNestedScroll(target: View, type: Int) {
        helper.onStopNestedScroll(target, type)
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return false
    }
}

private class MyHolder1(itemView: View) : RecyclerView.ViewHolder(itemView)

private class MyAdapter1(private var data : ArrayList<String>) : RecyclerView.Adapter<MyHolder1>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder1 {
        val view = TextView(parent.context)
        view.setTextColor(Color.WHITE)
        view.minHeight = 100
        view.gravity = Gravity.CENTER
        view.setBackgroundColor(ColorUtils.setAlphaComponent(Color.RED, (255 * 0.6f).toInt()))
        val rp = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        view.layoutParams = rp
        return MyHolder1(view)
    }

    override fun getItemCount(): Int {
        return 100
    }

    override fun onBindViewHolder(holder: MyHolder1, position: Int) {
        val tv = holder.itemView as TextView
        tv.text = getData(position)
    }

    private fun getData(index : Int) : String? {
        return data[index]
    }
}