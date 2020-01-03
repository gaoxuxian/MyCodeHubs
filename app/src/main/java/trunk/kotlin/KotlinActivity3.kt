package trunk.kotlin

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.xx.commonlib.PxUtil
import trunk.R
import java.util.*
import kotlin.collections.ArrayList

class NestedScrollActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PxUtil.init(this)
        Glide.get(this).clearMemory()

        val layout = MyLinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        val param = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        layout.layoutParams = param
        setContentView(layout)

//        window.attributes.width = 1080
//        window.attributes.height = 1080
//
//        val newlayout = object :FrameLayout(this) {
//            override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
//                return super.dispatchKeyEvent(event)
//            }
//
//            override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//                return super.dispatchTouchEvent(ev)
//            }
//        }
//        newlayout.setBackgroundColor(Color.RED)
//        var params = WindowManager.LayoutParams()
////        params.type = WindowManager.LayoutParams.TYPE_APPLICATION
//        params.token = window.attributes.token
////        params.copyFrom(window.attributes)
//        params.width = 540
//        params.height = 300
//        params.gravity = Gravity.BOTTOM.or(Gravity.CENTER_HORIZONTAL)
//        params.type = WindowManager.LayoutParams.TYPE_BASE_APPLICATION
//        windowManager.addView(newlayout, params)


        val imageView = ImageView(this)
//        imageView.setImageResource(R.drawable.open_test_9)
        var param1 = LinearLayout.LayoutParams(PxUtil.sU_1080p(1080), (PxUtil.sU_1080p(1080) * 9f / 16f).toInt())
        layout.addView(imageView, param1)
        val target = object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {
                imageView.setImageDrawable(placeholder)
            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                imageView.setImageBitmap(resource)
            }
        }
        imageView.postDelayed({ Glide.with(imageView.context).asBitmap().load(R.drawable.open_test_9).into(target) }, 2000)

        val data = ArrayList<String>()
        for (i in 1..100) {
            data.add(i.toString())
        }

        val adapter = MyAdapter1(data = data)
        val recyclerView = RecyclerView(this)
        recyclerView.overScrollMode = View.OVER_SCROLL_NEVER
        recyclerView.isNestedScrollingEnabled = true
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.set(5, 5, 5, 5)
            }
        })
//        param1 = LinearLayout.LayoutParams(PxUtil.sU_1080p(1080), PxUtil.sV_1080p(1920))
//        param1 = LinearLayout.LayoutParams(PxUtil.sU_1080p(1080), PxUtil.sU_1080p(1920))
        param1 = LinearLayout.LayoutParams(PxUtil.sU_1080p(1080), LinearLayout.LayoutParams.MATCH_PARENT)
        layout.addView(recyclerView, param1)

        val itemTouchHelper = ItemTouchHelper(MySimpleCallBack(adapter,
            ItemTouchHelper.UP.or(ItemTouchHelper.DOWN),
            ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)))
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Thread.dumpStack()
        return super.onTouchEvent(event)
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

private class MyAdapter1(private var data : ArrayList<String>) : RecyclerView.Adapter<MyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = TextView(parent.context)
        view.setTextColor(Color.WHITE)
        view.minHeight = 200
        view.gravity = Gravity.CENTER
        view.setBackgroundColor(ColorUtils.setAlphaComponent(Color.RED, (255 * 0.6f).toInt()))
        val rp = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        view.layoutParams = rp
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val tv = holder.itemView as TextView
        tv.text = getData(position)
    }

    private fun getData(index : Int) : String? {
        return data[index]
    }

    //交换item
    fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(data, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    //删除item
    fun onItemDismiss(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }
}

private class MySimpleCallBack(val adapter: MyAdapter1, dragDirs : Int, swipeDirs : Int) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        adapter.onItemMove(fromPosition = viewHolder.adapterPosition, toPosition = target.adapterPosition)
        Log.e("***", "MySimpleCallBack : onMove()")
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onItemDismiss(viewHolder.adapterPosition)
        Log.e("***", "MySimpleCallBack : onSwiped()")
    }

}