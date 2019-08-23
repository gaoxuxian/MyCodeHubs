package trunk.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.graphics.ColorUtils;
import trunk.BaseActivity;
import util.ImageUtils;
import util.PxUtil;

public class TouchEventActivity extends BaseActivity {

    @Override
    public void onCreateBaseData() throws Exception {

    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params) {
        FrameLayout layoutA = new FrameLayout(context) {
            @Override
            public boolean dispatchTouchEvent(MotionEvent ev) {
                return super.dispatchTouchEvent(ev);
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return super.onInterceptTouchEvent(ev);
            }

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                return super.onTouchEvent(event);
            }
        };
        layoutA.setBackgroundColor(ColorUtils.setAlphaComponent(Color.RED, (int) (255 * 1f)));
        params = new FrameLayout.LayoutParams(PxUtil.sU_1080p(810), PxUtil.sU_1080p(1620));
        params.gravity = Gravity.CENTER;
        parent.addView(layoutA, params);
        {
            FrameLayout layoutB = new FrameLayout(context) {
                float x;
                float y;

                @Override
                public boolean dispatchTouchEvent(MotionEvent ev) {
                    switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN: {
                            x = ev.getX();
                            y = ev.getY();
                            break;
                        }
                    }
                    return super.dispatchTouchEvent(ev);
                }

                @Override
                public boolean onInterceptTouchEvent(MotionEvent ev) {
                    boolean intercept = false;
                    switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_MOVE: {
                            float dx = Math.abs(ev.getX() - x);
                            float dy = Math.abs(ev.getY() - y);

                            if (ImageUtils.Spacing(dx, dy) > PxUtil.sU_1080p(100)) {
                                intercept = true;
                            }
                            break;
                        }
                    }
                    return intercept || super.onInterceptTouchEvent(ev);
                }

                @Override
                public boolean onTouchEvent(MotionEvent event) {
                    return super.onTouchEvent(event);
                }
            };
            layoutB.setBackgroundColor(ColorUtils.setAlphaComponent(Color.GREEN, (int) (255 * 1f)));
            params = new FrameLayout.LayoutParams(PxUtil.sU_1080p(540), PxUtil.sU_1080p(1080));
            params.gravity = Gravity.CENTER;
            layoutA.addView(layoutB, params);
            {
                View viewA = new View(context) {
                    @Override
                    public boolean dispatchTouchEvent(MotionEvent event) {
                        return super.dispatchTouchEvent(event);
                    }

                    @Override
                    public boolean onTouchEvent(MotionEvent event) {
                        Log.d("***", "onTouchEvent: viewA");
                        return true;
                    }

                    @Override
                    protected void onDraw(Canvas canvas) {
                        super.onDraw(canvas);
                    }
                };
                viewA.setBackgroundColor(ColorUtils.setAlphaComponent(Color.BLUE, (int) (255 * 1f)));
                params = new FrameLayout.LayoutParams(PxUtil.sU_1080p(270), PxUtil.sU_1080p(540));
                params.gravity = Gravity.CENTER;
                layoutB.addView(viewA, params);
            }
        }

    }

    @Override
    public void onCreateFinish() {

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
    }
}
