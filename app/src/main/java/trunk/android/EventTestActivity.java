package trunk.android;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.*;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import trunk.BaseActivity;
import util.PxUtil;

public class EventTestActivity extends BaseActivity {

    @Override
    public void onCreateBaseData() throws Exception {

    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params) {
        FrameLayout layout_a = new FrameLayout(context) {
            @Override
            public boolean dispatchTouchEvent(MotionEvent ev) {
                logDispatchTouchEvent(ev, "layout_a");
                return super.dispatchTouchEvent(ev);
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                Log.d("xxx", "onInterceptTouchEvent: --------> layout_a");
                return super.onInterceptTouchEvent(ev);
            }

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                logTouchEvent(event, "layout_a");
                //return super.onTouchEvent(event);
                return true;
            }
        };
        layout_a.setBackgroundColor(Color.GRAY);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(layout_a, params);
        {
            View view_a = new View(context){
                @Override
                public boolean dispatchTouchEvent(MotionEvent event) {
                    logDispatchTouchEvent(event, "view_a");
                    return super.dispatchTouchEvent(event);
                }

                @Override
                public boolean onTouchEvent(MotionEvent event) {
                    logTouchEvent(event, "view_a");
                    return super.onTouchEvent(event);
                }
            };
            view_a.setBackgroundColor(Color.BLUE);
            params = new FrameLayout.LayoutParams(PxUtil.sU_1080p(300), PxUtil.sU_1080p(300));
            layout_a.addView(view_a, params);

            FrameLayout layout_b = new FrameLayout(context) {
                @Override
                public boolean dispatchTouchEvent(MotionEvent ev) {
                    logDispatchTouchEvent(ev, "layout_b");
                    return super.dispatchTouchEvent(ev);
                }

                @Override
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    Log.d("xxx", "onInterceptTouchEvent: --------> layout_b");
                    return super.onInterceptTouchEvent(ev);
                }

                @Override
                public boolean onTouchEvent(MotionEvent event) {
                    logTouchEvent(event, "layout_b");
                    return super.onTouchEvent(event);
//                    return true;
                }
            };
            layout_b.setBackgroundColor(ColorUtils.setAlphaComponent(Color.BLUE, (int) (255 * 0.5f)));
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxUtil.sU_1080p(500));
            params.gravity = Gravity.CENTER;
            layout_a.addView(layout_b, params);
            {
                View view_b = new View(context){
                    @Override
                    public boolean dispatchTouchEvent(MotionEvent event) {
                        logDispatchTouchEvent(event, "view_b");
                        return super.dispatchTouchEvent(event);
                    }

                    @Override
                    public boolean onTouchEvent(MotionEvent event) {
                        logTouchEvent(event, "view_b");
                        return super.onTouchEvent(event);
                    }
                };
                view_b.setBackgroundColor(Color.BLUE);
                params = new FrameLayout.LayoutParams(PxUtil.sU_1080p(300), PxUtil.sU_1080p(300));
                layout_b.addView(view_b, params);
            }
        }
    }

    private void logDispatchTouchEvent(MotionEvent ev, String msg) {
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:{
                Log.d("xxx", "dispatchTouchEvent: MotionEvent.ACTION_DOWN ---------> " + msg);
                break;
            }

            case MotionEvent.ACTION_MOVE:{
                Log.d("xxx", "dispatchTouchEvent: MotionEvent.ACTION_MOVE ---------> " + msg);
                break;
            }

            case MotionEvent.ACTION_UP:{
                Log.d("xxx", "dispatchTouchEvent: MotionEvent.ACTION_UP ---------> " + msg);
                break;
            }
        }
    }

    private void logTouchEvent(MotionEvent ev, String msg) {
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:{
                Log.d("xxx", "onTouchEvent: MotionEvent.ACTION_DOWN ---------> " + msg);
                break;
            }

            case MotionEvent.ACTION_MOVE:{
                Log.d("xxx", "onTouchEvent: MotionEvent.ACTION_MOVE ---------> " + msg);
                break;
            }

            case MotionEvent.ACTION_UP:{
                Log.d("xxx", "onTouchEvent: MotionEvent.ACTION_UP ---------> " + msg);
                break;
            }
        }
    }

    @Override
    public void onCreateFinish() {

    }
}
