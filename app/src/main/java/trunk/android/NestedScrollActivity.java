package trunk.android;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;

import com.xx.commonlib.PxUtil;

import trunk.R;

public class NestedScrollActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PxUtil.init(this);

        MyFrameLayout layout = new MyFrameLayout(this);
        layout.setBackgroundColor(ColorUtils.setAlphaComponent(Color.BLUE, (int) (255 * .7f)));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);
        setContentView(layout);

        MyView view = new MyView(this);
        view.setImageResource(R.drawable.open_test_2);
        view.setBackgroundColor(Color.RED);
        params = new FrameLayout.LayoutParams(PxUtil.sU_1080p(800), PxUtil.sU_1080p(800));
        params.gravity = Gravity.CENTER;
        layout.addView(view, params);
    }

    private static class MyFrameLayout extends FrameLayout {

        public MyFrameLayout(@NonNull Context context) {
            super(context);
        }

        @Override
        public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
            return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_HORIZONTAL) != 0;
        }

        @Override
        public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
            boolean hiddenTop = dx > 0 && getScrollX() < PxUtil.sU_1080p(140);
            boolean showTop = dx < 0 && getScrollX() > 0;
//            boolean showTop = dx < 0 && getScrollX() > 0 && !ViewCompat.canScrollHorizontally(target, -1);

            if (hiddenTop /*|| showTop*/) {
                int x = PxUtil.sU_1080p(140) - getScrollX();
                if (dx <= x) {
                    scrollBy(dx, 0);
                    consumed[0] = dx;
                } else {
                    scrollBy(x, 0);
                    consumed[0] = dx - x;
                }
            } else if (showTop) {
                int x = getScrollX();
                if (dx >= -x) {
                    scrollBy(dx, 0);
                    consumed[0] = dx;
                } else {
                    scrollBy(-x, 0);
                    consumed[0] = dx - x;
                }
            }
        }

        @Override
        public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
            super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        }

        @Override
        public void onNestedScrollAccepted(View child, View target, int axes) {
            super.onNestedScrollAccepted(child, target, axes);
        }

        @Override
        public void onStopNestedScroll(View child) {
            super.onStopNestedScroll(child);
        }
    }

    private static class MyView extends ImageView {

        private int mLastTouchX;
        private int mLastTouchY;
        private int[] mNestedOffsets = new int[2];
        private int[] mReusableIntPair = new int[2];
        private int[] mScrollOffset = new int[2];

        public MyView(Context context) {
            super(context);
            setNestedScrollingEnabled(true);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int action = event.getAction() & MotionEvent.ACTION_MASK;
            if (action == MotionEvent.ACTION_DOWN) {
                mNestedOffsets[0] = mNestedOffsets[1] = 0;
            }
            final MotionEvent vtev = MotionEvent.obtain(event);
            vtev.offsetLocation(mNestedOffsets[0], mNestedOffsets[1]);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mLastTouchX = (int) (event.getX() + 0.5f);
                    mLastTouchY = (int) (event.getY() + 0.5f);
                    int nestedScrollAxis = ViewCompat.SCROLL_AXIS_NONE;
//                    if (canScrollHorizontally) {
                        nestedScrollAxis |= ViewCompat.SCROLL_AXIS_HORIZONTAL;
//                    }
//                    if (canScrollVertically) {
//                        nestedScrollAxis |= ViewCompat.SCROLL_AXIS_VERTICAL;
//                    }
                    startNestedScroll(nestedScrollAxis);
                    break;

                case MotionEvent.ACTION_MOVE:
                    final int x = (int) (event.getX() + 0.5f);
                    final int y = (int) (event.getY() + 0.5f);
                    int dx = mLastTouchX - x;
                    int dy = mLastTouchY - y;
                    mReusableIntPair[0] = 0;
                    mReusableIntPair[1] = 0;
                    if (dispatchNestedPreScroll(dx, 0, mReusableIntPair, mScrollOffset)) {
                        dx -= mReusableIntPair[0];
                        dy -= mReusableIntPair[1];
                        // Updated the nested offsets
                        mNestedOffsets[0] += mScrollOffset[0];
                        mNestedOffsets[1] += mScrollOffset[1];
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }

                    mLastTouchX = x - mScrollOffset[0];
                    mLastTouchY = y - mScrollOffset[1];
                    break;

                case MotionEvent.ACTION_UP:
                    stopNestedScroll();
                    break;
            }
            vtev.recycle();
            return true;
        }
    }
}
