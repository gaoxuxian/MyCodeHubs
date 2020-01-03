package trunk.gles;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xx.avlibrary.gl.filter.rhythm.DisplayFilter;
import com.xx.avlibrary.gl.filter.rhythm.FrameSizeFilter;
import com.xx.avlibrary.gl.filter.rhythm.FrameSizeHelper;
import com.xx.avlibrary.gl.filter.rhythm.FrameSizeType;
import com.xx.avlibrary.gl.filter.rhythm.TextureFilter;
import com.xx.avlibrary.gl.util.GLUtil;
import com.xx.commonlib.ImageUtils;
import com.xx.commonlib.PxUtil;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import trunk.BaseActivity;
import trunk.R;

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
    final float DEGREE = -90;
    float mUIDegree;

    private boolean mDoingAnim;
    private boolean mPointerTouch;

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
            mFrameSizeView.setGestureDetector(new MyFrameSizeView.GestureListener() {
                @Override
                public void onDown() {

                }

                @Override
                public void onFingersDown() {
                    mPointerTouch = true;
//                    if (!mDoingAnim) {
//                        releaseToGesture(true);
//                    }
                }

                @Override
                public void onMove(float scale, float transX, float transY) {
                    if (!mDoingAnim) {
                        updateGestureData(scale, transX, transY);
                        requestToGesture();
                    }
                }

//                @Override
//                public void onMove(float transX, float transY) {
//                    requestToGesture();
//                    updateGestureData(1f, transX, transY);
//                }
//
//                @Override
//                public void onZoom(float scale) {
//                    if (!mDoingAnim) {
//                        requestToGesture();
//                        updateGestureData(scale, 0, 0);
//                    }
//                }

                @Override
                public void onUp() {
                    // FIXME: 2019/5/4 onFingersUp 和 onUp 同时触发，画面会飘，触发了两次数据同步
                    // FIXME: 2019/5/4 试下加锁
                    if (!mDoingAnim) {
                        releaseToGesture(true);
                    }
                }

                @Override
                public void onFingersUp() {
                    if (!mDoingAnim) {
                        releaseToGesture(true);
                    }

                    mPointerTouch = false;
                }
            });
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
                        public void onAnimationStart(Animator animation) {
                            mDoingAnim = true;
                            if (mPointerTouch) {
                                releaseToGesture(true);
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            setScaleType(FrameSizeHelper.scale_type_full_in);
                            if (mPointerTouch) {
                                mFrameSizeView.updatePointer();
                            }
                            mDoingAnim = false;
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
                        public void onAnimationStart(Animator animation) {
                            mDoingAnim = true;
                            if (mPointerTouch) {
                                releaseToGesture(true);
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            setScaleType(FrameSizeHelper.scale_type_not_full_in);
                            if (mPointerTouch) {
                                mFrameSizeView.updatePointer();
                            }
                            mDoingAnim = false;
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
                            requestToDoScaleAnim(FrameSizeHelper.scale_type_full_in, value);
                            requestToRotateAnim(mUIDegree + DEGREE, degree +  DEGREE * value, value);
                        }
                    });
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mDoingAnim = true;
                            if (mPointerTouch) {
                                releaseToGesture(true);
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mUIDegree += DEGREE;
                            setRotation(mUIDegree);
                            setScaleType(FrameSizeHelper.scale_type_full_in);
                            if (mPointerTouch) {
                                mFrameSizeView.updatePointer();
                            }
                            mDoingAnim = false;

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
        setScaleType(FrameSizeHelper.scale_type_full_in);
        mFrameSizeFilter.setVideoFrameSize(size);
    }

    private void setScaleType(int type) {
        mFrameSizeFilter.setScaleType(type);
        mFrameSizeFilter.releaseAnim();
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
        mFrameSizeFilter.releaseAnim();
    }

    private void requestToGesture() {
        mFrameSizeFilter.requestGesture();
    }

    private void updateGestureData(float scale, float transX, float transY) {
        mFrameSizeFilter.updateGestureData(scale, transX, transY);
    }

    private void releaseToGesture(boolean sync) {
        mFrameSizeFilter.releaseGesture();
        if (sync) {
            mFrameSizeFilter.syncGestureData();
        }
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
        mFrameSizeFilter.setVideoFrameSize(mFrameSize);
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
            int texture = mBmpToTextureFilter.createGlTexture(R.drawable.open_test_9);
            mBmpToTextureFilter.initFrameBufferOfTextureSize(); // FIXME: 2019/4/30 调整纹理尺寸, 调整成图片一半，或者指定大小
            texture = mBmpToTextureFilter.onDrawBuffer(texture);

            if (mFrameSizeFilter != null) {
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

        public interface GestureListener {
            void onDown();

            void onFingersDown();

            void onMove(float scale, float transX, float transY);

//            void onZoom(float scale);

            void onUp();

            void onFingersUp();
        }

        public MyFrameSizeView(Context context) {
            super(context);
            mRect = new Rect();
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(0x991a1a1a);
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

        GestureListener mGestureDetector;

        public void setGestureDetector(GestureListener detector) {
            mGestureDetector = detector;
        }

        int mFrameSize;

        public void setFrameSize(int size) {
            mFrameSize = size;
            invalidate();
        }

        private float mDownX;
        private float mDownY;

        private float mPointerDownX;
        private float mPointerDownY;

        private boolean mResetPointer;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            boolean out = false;

            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN: {
                    mDownX = event.getX();
                    mDownY = event.getY();
                    if (mGestureDetector != null) {
                        mGestureDetector.onDown();
                    }
                    out = true;
                    break;
                }

                case MotionEvent.ACTION_POINTER_DOWN: {
                    mDownX = event.getX(0);
                    mDownY = event.getY(0);
                    mPointerDownX = event.getX(1);
                    mPointerDownY = event.getY(1);
                    if (mGestureDetector != null) {
                        mGestureDetector.onFingersDown();
                    }
                    out = true;
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    float scale = 1f;
                    float x = 0;
                    float y = 0;
                    if (event.getPointerCount() >= 2) {
                        if (mUpdatePointer) {
                            mDownX = event.getX(0);
                            mDownY = event.getY(0);
                            mPointerDownX = event.getX(1);
                            mPointerDownY = event.getY(1);
                            mUpdatePointer = false;
                        }
                        float x1 = event.getX(0);
                        float y1 = event.getY(0);
                        float x2 = event.getX(1);
                        float y2 = event.getY(1);

                        float moveSpacing = ImageUtils.Spacing(x2 - x1, y2 - y1);
                        float downSpacing = ImageUtils.Spacing(mPointerDownX - mDownX, mPointerDownY - mDownY);
                        scale = moveSpacing / downSpacing;

//                        x = ((x1 + x2) * 0.5f - (mPointerDownX + mDownX) * 0.5f) * 2 / getMeasuredWidth();
//                        y = ((y1 + y2) * 0.5f - (mPointerDownY + mDownY) * 0.5f) * 2 / getMeasuredHeight();

                    } else {
                        x = (event.getX() - mDownX) * 2 / getMeasuredWidth();
                        y = (event.getY() - mDownY) * 2 / getMeasuredHeight();
                    }
                    if (mGestureDetector != null) {
                        mGestureDetector.onMove(scale,x, y);
                    }

                    out = true;
                    break;
                }

                case MotionEvent.ACTION_UP: {
                    out = true;
                    if (mGestureDetector != null) {
                        mGestureDetector.onUp();
                    }
                    break;
                }

                case MotionEvent.ACTION_POINTER_UP: {
                    mDownX = event.getX();
                    mDownY = event.getY();
                    if (mGestureDetector != null) {
                        mGestureDetector.onFingersUp();
                    }
                    out = true;
                    break;
                }
            }

            return out || onTrackballEvent(event);
        }

        boolean mUpdatePointer;

        public void updatePointer() {
            mUpdatePointer = true;
        }
    }
}
