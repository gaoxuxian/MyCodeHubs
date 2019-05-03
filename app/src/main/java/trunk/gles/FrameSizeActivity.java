package trunk.gles;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import lib.gl.filter.rhythm.*;
import lib.gl.util.GLUtil;
import trunk.BaseActivity;
import trunk.R;
import util.PxUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;

public class FrameSizeActivity extends BaseActivity implements GLSurfaceView.Renderer {
    RecyclerView mFrameSizeListView;
    GLSurfaceView mGlView;
    MyFrameSizeView mFrameSizeView;
    private Button startBtn;
    private Button pauseBtn;
    private Button fullInBtn;
    private Button notFullInBtn;
    private Button showSizeBtn;
    private Button unshowSizeBtn;
    private Button rotationBtn;

    ArrayList<FrameSizeInfo> mFrameSizeData;

    boolean mCanDraw;
    final float DEGREE = 90;
    float mUIDegree;

    @Override
    public void onCreateBaseData() throws Exception {
        mFrameSizeData = new ArrayList<>();
        FrameSizeInfo info = new FrameSizeInfo();
        info.frameSizeType = FrameSizeType.size_1_1;
        info.name = "1:1";
        mFrameSizeData.add(info);

        info = new FrameSizeInfo();
        info.frameSizeType = FrameSizeType.size_3_4;
        info.name = "3:4";
        mFrameSizeData.add(info);

        info = new FrameSizeInfo();
        info.frameSizeType = FrameSizeType.size_4_3;
        info.name = "4:3";
        mFrameSizeData.add(info);

        info = new FrameSizeInfo();
        info.frameSizeType = FrameSizeType.size_9_16;
        info.name = "9:16";
        mFrameSizeData.add(info);

        info = new FrameSizeInfo();
        info.frameSizeType = FrameSizeType.size_16_9;
        info.name = "16:9";
        mFrameSizeData.add(info);

        info = new FrameSizeInfo();
        info.frameSizeType = FrameSizeType.size_235_1;
        info.name = "2.35:1";
        mFrameSizeData.add(info);
    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params) {
        ConstraintLayout layout = new ConstraintLayout(context);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(layout, params);
        {
            ConstraintLayout.LayoutParams cl = null;

            mFrameSizeListView = new RecyclerView(context);
            mFrameSizeListView.setId(View.generateViewId());
            mFrameSizeListView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            cl = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxUtil.sU_1080p(200));
            cl.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            cl.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            cl.topMargin = PxUtil.sU_1080p(100);
            layout.addView(mFrameSizeListView, cl);

            MyAdapter adapter = new MyAdapter();
            adapter.setData(mFrameSizeData);
            adapter.setListener(new MyAdapter.Listener() {
                @Override
                public void onClick(FrameSizeInfo info) {
                    setFrameSize(info.frameSizeType);
                    mFrameSizeView.setFrameSize(info.frameSizeType);
                }
            });
            mFrameSizeListView.setAdapter(adapter);

            mGlView = new GLSurfaceView(context);
            mGlView.setId(View.generateViewId());
            mGlView.setEGLContextClientVersion(GLUtil.getGlSupportVersionInt(context));
            mGlView.setRenderer(this);
            mGlView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            cl = new ConstraintLayout.LayoutParams(PxUtil.sU_1080p(1080), PxUtil.sU_1080p(1080));
            cl.topToBottom = mFrameSizeListView.getId();
            cl.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            layout.addView(mGlView, cl);

            mFrameSizeView = new MyFrameSizeView(context);
            mFrameSizeView.setId(View.generateViewId());
            mFrameSizeView.setFrameSize(mFrameSize);
            mFrameSizeView.setGestureDetector(new GestureDetector(context, new GestureDetector.OnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public void onShowPress(MotionEvent e) {

                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    Log.d("xxx", "onScroll: ======================================");
                    Log.d("xxx", "onScroll: e1 == " + e1);
                    Log.d("xxx", "onScroll: e2 == " + e2);
                    Log.d("xxx", "onScroll: distanceX == " + distanceX);
                    Log.d("xxx", "onScroll: distanceY == " + distanceY);

                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {

                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    return false;
                }
            }));
            cl = new ConstraintLayout.LayoutParams(PxUtil.sU_1080p(1080), PxUtil.sU_1080p(1080));
            cl.topToTop = mGlView.getId();
            cl.leftToLeft = mGlView.getId();
            cl.rightToRight = mGlView.getId();
            layout.addView(mFrameSizeView, cl);

            startBtn = new Button(context);
            startBtn.setId(View.generateViewId());
            startBtn.setAllCaps(false);
            startBtn.setText("start");
            startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCanDraw = true;
                    mGlView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                    mGlView.requestRender();
                }
            });
            cl = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cl.topToBottom = mFrameSizeView.getId();
            cl.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            cl.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            layout.addView(startBtn, cl);

            pauseBtn = new Button(context);
            pauseBtn.setId(View.generateViewId());
            pauseBtn.setAllCaps(false);
            pauseBtn.setText("pause");
            pauseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCanDraw = false;
                    mGlView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                }
            });
            cl = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cl.topToBottom = mFrameSizeView.getId();
            cl.leftToRight = startBtn.getId();
            layout.addView(pauseBtn, cl);

            fullInBtn = new Button(context);
            fullInBtn.setId(View.generateViewId());
            fullInBtn.setAllCaps(false);
            fullInBtn.setText("fullIn");
            fullInBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
                    animator.setDuration(250);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float value = (float) animation.getAnimatedValue();
                            requestToDoScaleAnim(FrameSizeHelper.scale_type_full_in, value);
                        }
                    });
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            setScaleType(FrameSizeHelper.scale_type_full_in);
                        }
                    });
                    animator.start();
                }
            });
            cl = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cl.topToBottom = startBtn.getId();
            cl.leftToLeft = startBtn.getId();
            layout.addView(fullInBtn, cl);

            notFullInBtn = new Button(context);
            notFullInBtn.setId(View.generateViewId());
            notFullInBtn.setAllCaps(false);
            notFullInBtn.setText("not fullIn");
            notFullInBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
                    animator.setDuration(250);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float value = (float) animation.getAnimatedValue();
                            requestToDoScaleAnim(FrameSizeHelper.scale_type_not_full_in, value);
                        }
                    });
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            setScaleType(FrameSizeHelper.scale_type_not_full_in);
                        }
                    });
                    animator.start();
                }
            });
            cl = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cl.topToBottom = pauseBtn.getId();
            cl.leftToRight = fullInBtn.getId();
            layout.addView(notFullInBtn, cl);

            showSizeBtn = new Button(context);
            showSizeBtn.setId(View.generateViewId());
            showSizeBtn.setAllCaps(false);
            showSizeBtn.setText("cut frame size");
            showSizeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setCutFrameSize(true);
                }
            });
            cl = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cl.topToBottom = fullInBtn.getId();
            cl.leftToLeft = fullInBtn.getId();
            layout.addView(showSizeBtn, cl);

            unshowSizeBtn = new Button(context);
            unshowSizeBtn.setId(View.generateViewId());
            unshowSizeBtn.setAllCaps(false);
            unshowSizeBtn.setText("uncut frame size");
            unshowSizeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setCutFrameSize(false);
                }
            });
            cl = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cl.topToBottom = notFullInBtn.getId();
            cl.leftToRight = showSizeBtn.getId();
            layout.addView(unshowSizeBtn, cl);

            rotationBtn = new Button(context);
            rotationBtn.setId(View.generateViewId());
            rotationBtn.setAllCaps(false);
            rotationBtn.setText("add "+ DEGREE + "°");
            rotationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float value = (float) animation.getAnimatedValue();
                            float degree = mUIDegree;
                            requestToRotateAnim(mUIDegree + DEGREE, degree +  DEGREE * value, value);
                        }
                    });
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mUIDegree += DEGREE;
                            setRotation(mUIDegree);

                        }
                    });
                    animator.setDuration(250);
                    animator.start();

//                    mDegree += DEGREE;
                }
            });
            cl = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cl.topToBottom = showSizeBtn.getId();
            cl.leftToLeft = showSizeBtn.getId();
            layout.addView(rotationBtn, cl);
        }
    }

    @Override
    public void onCreateFinish() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mGlView != null) {
            mGlView.onPause();
        }

        if (mBmpToTextureFilter != null) {
            mBmpToTextureFilter.destroy();
        }

        if (mFrameSizeFilter != null) {
            mFrameSizeFilter.destroy();
        }

        if (mDisplayFilter != null) {
            mDisplayFilter.destroy();
        }
    }

    int mFrameSize = FrameSizeType.size_3_4;

    private void setFrameSize(int size) {
        mFrameSize = size;
        setScaleType(FrameSizeHelper.scale_type_full_in);
    }

    private void setScaleType(int type) {
        mFrameSizeFilter.setScaleType(type);
    }

    private void requestToDoScaleAnim(int nextScaleType, float factor) {
        mFrameSizeFilter.requestToDoScaleAnim(nextScaleType, factor);
    }

    private void setCutFrameSize(boolean cut) {
        mFrameSizeFilter.setFrameSizeCut(!cut);
    }

    private void requestToRotateAnim(float nextDegree, float currentDegree, float factor) {
        mFrameSizeFilter.requestToRotateAnim(nextDegree, currentDegree, factor);
    }

    private void setRotation(float degree) {
        mFrameSizeFilter.setRotation(degree);
    }

    TextureFilter mBmpToTextureFilter;
    FrameSizeFilter mFrameSizeFilter;
    DisplayFilter mDisplayFilter;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mBmpToTextureFilter = new TextureFilter(this);
        mBmpToTextureFilter.onSurfaceCreated(null);

        mFrameSizeFilter = new FrameSizeFilter(this);
        mFrameSizeFilter.onSurfaceCreated(null);
        mFrameSizeFilter.setScaleType(FrameSizeHelper.scale_type_full_in);
        mFrameSizeFilter.setRotation(0);

        mDisplayFilter = new DisplayFilter(this);
        mDisplayFilter.onSurfaceCreated(null);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mBmpToTextureFilter.onSurfaceChanged(width, height);

        mFrameSizeFilter.onSurfaceChanged(width, height);
        mFrameSizeFilter.initFrameBuffer(width, height);

        mDisplayFilter.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mCanDraw) {
            int texture = mBmpToTextureFilter.createGlTexture(R.drawable.open_test_6);
            mBmpToTextureFilter.initFrameBufferOfTextureSize(); // FIXME: 2019/4/30 调整纹理尺寸, 调整成图片一半，或者指定大小
            texture = mBmpToTextureFilter.onDrawBuffer(texture);

            if (mFrameSizeFilter != null) {
                mFrameSizeFilter.setVideoFrameSize(mFrameSize);
                mFrameSizeFilter.setTextureWH(mBmpToTextureFilter.getTextureW(), mBmpToTextureFilter.getTextureH());
                texture = mFrameSizeFilter.onDrawBuffer(texture);
                mDisplayFilter.setTextureWH(mFrameSizeFilter.getTextureW(), mFrameSizeFilter.getTextureH());
            }

            mDisplayFilter.onDrawFrame(texture);
        }
    }

    private static class FrameSizeInfo {
        int frameSizeType;
        String name;
    }

    private static class MyAdapter extends RecyclerView.Adapter implements RecyclerView.OnClickListener {

        ArrayList<FrameSizeInfo> mData;
        Listener mListener;

        public interface Listener {
            void onClick(FrameSizeInfo info);
        }

        public void setListener(Listener listener) {
            mListener = listener;
        }

        public void setData(ArrayList<FrameSizeInfo> data) {
            mData = data;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Button item = new Button(parent.getContext());
            item.setOnClickListener(this);
            ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            item.setLayoutParams(params);
            return new RecyclerView.ViewHolder(item) {};
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Button itemView = (Button) holder.itemView;
            itemView.setTag(position);

            FrameSizeInfo info = mData.get(position);
            itemView.setText(info.name);
        }

        @Override
        public int getItemCount() {
            return mData != null ? mData.size() : 0;
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            FrameSizeInfo info = mData.get(position);
            if (mListener != null) {
                mListener.onClick(info);
            }
        }
    }

    private static class MyFrameSizeView extends View {

        Rect mRect;
        Paint mPaint;

        public MyFrameSizeView(Context context) {
            super(context);
            mRect = new Rect();
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(ColorUtils.setAlphaComponent(Color.RED, (int) (255*0.3f)));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float aspectRatio = FrameSizeType.getAspectRatio(mFrameSize);

            int width = getMeasuredWidth();
            int height = getMeasuredHeight();

            int frameW = width;
            int frameH = (int) (width / aspectRatio);
            if (frameH > height) {
                frameW = (int) (height * aspectRatio);
                frameH = height;
            }

            if (frameW == width) {
                mRect.setEmpty();
                mRect.set(0, 0, frameW, (height - frameH)/2);
                canvas.drawRect(mRect, mPaint);

                mRect.setEmpty();
                mRect.set(0, (height + frameH) / 2, frameW, height);
                canvas.drawRect(mRect, mPaint);
            } else {
                mRect.setEmpty();
                mRect.set(0, 0, (width - frameW)/2, height);
                canvas.drawRect(mRect, mPaint);

                mRect.setEmpty();
                mRect.set((width + frameW)/2, 0, width, height);
                canvas.drawRect(mRect, mPaint);
            }
        }

        GestureDetector mGestureDetector;

        public void setGestureDetector(GestureDetector detector) {
            mGestureDetector = detector;
        }

        int mFrameSize;

        public void setFrameSize(int size) {
            mFrameSize = size;
            invalidate();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
//            return super.onTouchEvent(event);
            return mGestureDetector.onTouchEvent(event);
        }
    }
}
