package trunk.gles;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import lib.gl.filter.rhythm.*;
import lib.gl.util.GLUtil;
import trunk.BaseActivity;
import trunk.R;
import util.ImageUtils;
import util.PxUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.util.ArrayList;

public class FrameSizeV2Activity extends BaseActivity implements GLSurfaceView.Renderer {
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
    private Button changeImageBtn;
    private Button fullScreenBtn;

    ArrayList<FrameSizeInfo> mFrameSizeData;

    boolean mCanDraw;
    final float DEGREE = 90;

    private boolean mDoingAnim;
    private boolean mPointerTouch;

    private ArrayList<ImageInfo> images;
    private int mImageIndex;

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

        images = new ArrayList<>();
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.res = R.drawable.open_test_6;
        imageInfo.frame = new GLFrame();
        images.add(imageInfo);
        mImageInfo = imageInfo;

        imageInfo = new ImageInfo();
        imageInfo.res = R.drawable.open_test_9;
        imageInfo.frame = new GLFrame();
        images.add(imageInfo);

        imageInfo = new ImageInfo();
        imageInfo.res = R.drawable.open_test_9_1_1;
        imageInfo.frame = new GLFrame();
        images.add(imageInfo);
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
//            cl.topMargin = PxUtil.sU_1080p(100);
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
            cl = new ConstraintLayout.LayoutParams(PxUtil.sU_1080p(720), PxUtil.sU_1080p(1440));
//            cl.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            cl.topToBottom = mFrameSizeListView.getId();
            cl.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            cl.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            layout.addView(mGlView, cl);

            mFrameSizeView = new MyFrameSizeView(context);
            mFrameSizeView.setId(View.generateViewId());
            mFrameSizeView.setFrameSize(mFrameSize);
            mFrameSizeView.setGestureDetector(new MyFrameSizeView.GestureListener() {
                @Override
                public void onDown() {
                    if (mLogicExecutor != null) {
                        mLogicExecutor.requestGesture();
                    }
                }

                @Override
                public void onFingersDown() {
                    if (mLogicExecutor != null) {
                        mLogicExecutor.syncGestureTranslation(images.get(mImageIndex).frame);
                    }
                }

                @Override
                public void onMove(float transX, float transY) {
                    if (mLogicExecutor != null) {
                        mLogicExecutor.updateGestureTranslation(transX, transY);
                    }
                }

                @Override
                public void onScale(float scale) {
                    if (mLogicExecutor != null) {
                        mLogicExecutor.updateGestureScale(scale);
                    }
                }

                @Override
                public void onUp() {
                    if (mLogicExecutor != null) {
                        mLogicExecutor.releaseGesture(true, images.get(mImageIndex).frame);
                    }
                }

                @Override
                public void onFingersUp() {
                    if (mLogicExecutor != null) {
                        mLogicExecutor.syncGestureScale(images.get(mImageIndex).frame);
                    }
                }
            });
            cl = new ConstraintLayout.LayoutParams(PxUtil.sU_1080p(720), PxUtil.sU_1080p(1440));
            cl.topToTop = mGlView.getId();
            cl.leftToLeft = mGlView.getId();
            cl.rightToRight = mGlView.getId();
            cl.bottomToBottom = mGlView.getId();
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
                            updateAnimFactor(value);
                        }
                    });
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mDoingAnim = true;
                            if (mPointerTouch) {
                                releaseToGesture(true);
                            }
                            setScaleType(FrameBase.scale_type_full_in);
                            setSweptAngle(0);
                            requestAnim();
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            setScaleType(FrameBase.scale_type_full_in);
                            if (mImageInfo != null) {
                                mImageInfo.frame.setScaleType(FrameBase.scale_type_full_in);
                            }
                            releaseAnim();
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
                            updateAnimFactor(value);
                        }
                    });
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mDoingAnim = true;
                            if (mPointerTouch) {
                                releaseToGesture(true);
                            }
                            setScaleType(FrameBase.scale_type_not_full_in);
                            setSweptAngle(0);
                            requestAnim();
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (mImageInfo != null) {
                                mImageInfo.frame.setScaleType(FrameBase.scale_type_not_full_in);
                            }
                            releaseAnim();
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
                            updateAnimFactor(value);
                        }
                    });
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            mDoingAnim = true;
                            if (mPointerTouch) {
                                releaseToGesture(true);
                            }
                            setSweptAngle(DEGREE);
                            setScaleType(FrameBase.scale_type_full_in);
                            requestAnim();
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (mImageInfo != null) {
                                mImageInfo.frame.setScaleType(FrameBase.scale_type_full_in);
                                mImageInfo.frame.setDegree(mImageInfo.frame.getDegree() + DEGREE);
                            }
                            releaseAnim();
                            mDoingAnim = false;

                        }
                    });
                    animator.setDuration(2000);
                    animator.start();
                }
            });
            cl = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cl.topToBottom = showSizeBtn.getId();
            cl.leftToLeft = showSizeBtn.getId();
            layout.addView(rotationBtn, cl);

            changeImageBtn = new Button(context);
            changeImageBtn.setId(View.generateViewId());
            changeImageBtn.setAllCaps(false);
            changeImageBtn.setText("换图");
            changeImageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mImageIndex ++;
                    mImageIndex = mImageIndex % images.size();
                    mImageInfo = images.get(mImageIndex);
                }
            });
            cl = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cl.topToBottom = showSizeBtn.getId();
            cl.leftToRight = rotationBtn.getId();
            layout.addView(changeImageBtn, cl);

            fullScreenBtn = new Button(context);
            fullScreenBtn.setId(View.generateViewId());
            fullScreenBtn.setAllCaps(false);
            fullScreenBtn.setText("全屏");
            fullScreenBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setFullScreen();
                }
            });
            cl = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cl.topToBottom = showSizeBtn.getId();
            cl.rightToLeft = rotationBtn.getId();
            layout.addView(fullScreenBtn, cl);
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

    int mFrameSize = FrameSizeType.size_235_1;
    private ImageInfo mImageInfo;

    private void setFrameSize(int size) {
        setScaleType(FrameSizeHelper.scale_type_full_in);
        mFrameSizeFilter.setVideoFrameSize(size);
    }

    private void setScaleType(int type) {
        mLogicExecutor.setScaleType(type);
    }

    private void setSweptAngle(@FloatRange(from = 0) float sweptAngle) {
        mLogicExecutor.setSweptAngle(sweptAngle);
    }

    public void updateAnimFactor(float factor) {
        mLogicExecutor.updateAnimFactor(factor);
    }

    private void requestAnim() {
        mLogicExecutor.requestAnim();
    }

    private void releaseAnim() {
        mLogicExecutor.releaseAnim();
    }

    private void setCutFrameSize(boolean cut) {
        mFrameSizeFilter.setFrameSizeCut(!cut);
    }

    private void requestToGesture() {

    }

    private void updateGestureData(float scale, float transX, float transY) {

    }

    private void releaseToGesture(boolean sync) {

    }

    private float degree;
    private void setFullScreen() {
//        ValueAnimator animator = ValueAnimator.ofFloat(degree, degree + 90);
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float value = (float) animation.getAnimatedValue();
//                mGlView.setRotation(value);
//                mFrameSizeView.setRotation(value);
//                mDisplayFilter.setDegree(value);
//            }
//        });
//        animator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                degree += 90;
//            }
//        });
//        animator.setDuration(2000);
//        animator.start();

        ViewGroup.LayoutParams layoutParams = mGlView.getLayoutParams();
        layoutParams.width = PxUtil.sU_1080p(1080);
        layoutParams.height = PxUtil.sU_1080p(2160);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) layoutParams;
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        mGlView.requestLayout();

        layoutParams = mFrameSizeView.getLayoutParams();
        layoutParams.width = PxUtil.sU_1080p(1080);
        layoutParams.height = PxUtil.sU_1080p(2160);
        mFrameSizeView.requestLayout();
//        mGlView.setRotation(90);
//        mFrameSizeView.setRotation(90);
//        mDisplayFilter.setDegree(90);
    }

    TextureFilter mBmpToTextureFilter;
    FrameSizeFilterV2 mFrameSizeFilter;
    DisplayFilter mDisplayFilter;
    FrameLogicExecutor mLogicExecutor;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mBmpToTextureFilter = new TextureFilter(this);
        mBmpToTextureFilter.onSurfaceCreated(null);

        mLogicExecutor = new FrameLogicExecutor();

        mFrameSizeFilter = new FrameSizeFilterV2(this, mLogicExecutor);
        mFrameSizeFilter.onSurfaceCreated(null);
        mFrameSizeFilter.setVideoFrameSize(mFrameSize);

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
            int texture = mBmpToTextureFilter.createGlTexture(mImageInfo.res);
            mBmpToTextureFilter.initFrameBufferOfTextureSize(); // FIXME: 2019/4/30 调整纹理尺寸, 调整成图片一半，或者指定大小
            texture = mBmpToTextureFilter.onDrawBuffer(texture);

            if (mFrameSizeFilter != null) {
                mFrameSizeFilter.setFrameBase(mImageInfo.frame);
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

    private static class ImageInfo {
        int res;
        GLFrame frame;
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

            void onMove(float transX, float transY);

            void onScale(float scale);

            void onUp();

            void onFingersUp();
        }

        public MyFrameSizeView(Context context) {
            super(context);
            mRect = new Rect();
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(0x991a1a1a);
            mPaint.setColor(ColorUtils.setAlphaComponent(Color.RED, (int) (255 * 0.3f)));
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

        private int mFirstPointerId;
        private int mSecondPointerId;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            boolean out = false;

            int actionIndex = event.getActionIndex();

            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN: {
                    mFirstPointerId = event.getPointerId(actionIndex);
                    mDownX = event.getX();
                    mDownY = event.getY();
                    if (mGestureDetector != null) {
                        mGestureDetector.onDown();
                    }
                    out = true;
                    break;
                }

                case MotionEvent.ACTION_POINTER_DOWN: {
                    mSecondPointerId = event.getPointerId(actionIndex);
                    mDownX = event.getX(event.findPointerIndex(mFirstPointerId));
                    mDownY = event.getY(event.findPointerIndex(mFirstPointerId));
                    mPointerDownX = event.getX(event.findPointerIndex(mSecondPointerId));
                    mPointerDownY = event.getY(event.findPointerIndex(mSecondPointerId));
                    if (mGestureDetector != null) {
                        mGestureDetector.onFingersDown();
                    }
                    out = true;
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    if (event.getPointerCount() >= 2) {
                        float x1 = event.getX(event.findPointerIndex(mFirstPointerId));
                        float y1 = event.getY(event.findPointerIndex(mFirstPointerId));
                        float x2 = event.getX(event.findPointerIndex(mSecondPointerId));
                        float y2 = event.getY(event.findPointerIndex(mSecondPointerId));

                        float moveSpacing = ImageUtils.Spacing(x2 - x1, y2 - y1);
                        float downSpacing = ImageUtils.Spacing(mPointerDownX - mDownX, mPointerDownY - mDownY);
                        if (mGestureDetector != null) {
                            mGestureDetector.onScale(moveSpacing / downSpacing);
                        }
                    } else {
                        float x = (event.getX() - mDownX) * 2 / getMeasuredWidth();
                        float y = (event.getY() - mDownY) * 2 / getMeasuredHeight();
                        if (mGestureDetector != null) {
                            mGestureDetector.onMove(x, y);
                        }
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
                    int firstPointerIndex = event.findPointerIndex(mFirstPointerId);
                    int secondPointerIndex = event.findPointerIndex(mSecondPointerId);

                    if (actionIndex == firstPointerIndex) {
                        mFirstPointerId = event.getPointerId(secondPointerIndex);
                        if (mGestureDetector != null) {
                            mGestureDetector.onFingersUp();
                        }
                    } else if (actionIndex == secondPointerIndex) {
                        mSecondPointerId = -1;
                        if (mGestureDetector != null) {
                            mGestureDetector.onFingersUp();
                        }
                    }

                    mDownX = event.getX(event.findPointerIndex(mFirstPointerId));
                    mDownY = event.getY(event.findPointerIndex(mFirstPointerId));
                    out = true;
                    break;
                }
            }

            return out || onTrackballEvent(event);
        }

        public void destroy() {
            mGestureDetector = null;
        }
    }
}
