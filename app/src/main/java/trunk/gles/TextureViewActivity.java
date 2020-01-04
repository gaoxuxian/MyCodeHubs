package trunk.gles;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.xx.avlibrary.gl.egl.EglCore;
import com.xx.avlibrary.gl.egl.EglSurfaceBase;
import com.xx.avlibrary.gl.filter.common.BmpToTextureFilter;
import com.xx.avlibrary.gl.filter.common.DisplayImageFilter;
import com.xx.commonlib.PxUtil;
import com.xx.commonlib.ThreadUtil;

import trunk.BaseActivity;
import trunk.R;

public class TextureViewActivity extends BaseActivity implements TextureView.SurfaceTextureListener {

    private TextureView mItemView;

    @Override
    public void onCreateBaseData() throws Exception {

    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params) {
        mItemView = new TextureView(context);
        mItemView.setSurfaceTextureListener(this);
        params = new FrameLayout.LayoutParams(PxUtil.sU_1080p(1080), PxUtil.sU_1080p(1080));
        params.gravity = Gravity.CENTER;
        parent.addView(mItemView, params);

        Button button = new Button(context);
        button.setText("改变TextureView宽高");
        button.setAllCaps(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams layoutParams = mItemView.getLayoutParams();
                layoutParams.height = PxUtil.sU_1080p(1920);
                mItemView.requestLayout();
            }
        });
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.addView(button, params);
    }

    @Override
    public void onCreateFinish() {

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d("xxx", "onSurfaceTextureAvailable: surface == " + surface + " , width == " + width + " , height == " + height);

        ThreadUtil.runOnUiThreadDelay(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Context context = mItemView.getContext();
                        if (mItemView.isAvailable()) {
                            SurfaceTexture surfaceTexture = mItemView.getSurfaceTexture();
                            EglCore eglCore = new EglCore();
                            eglCore.setConfig(context, 8, 8, 8, 8, 0, 0, false);
                            eglCore.initEglContext(null);

                            EglSurfaceBase base = new EglSurfaceBase(eglCore);
                            base.createWindowSurface(new Surface(surfaceTexture));
                            base.makeCurrent();

                            int width = mItemView.getMeasuredWidth();
                            int height = mItemView.getMeasuredHeight();

                            BmpToTextureFilter filter = new BmpToTextureFilter(context);
                            filter.onSurfaceCreated(null);
                            filter.onSurfaceChanged(width, height);

                            DisplayImageFilter displayImageFilter = new DisplayImageFilter(context);
                            displayImageFilter.onSurfaceCreated(null);
                            displayImageFilter.onSurfaceChanged(width, height);

                            filter.setBitmapRes(R.drawable.open_test_7);
                            filter.initFrameBufferOfTextureSize();
                            int i = filter.onDrawBuffer(0);

                            GLES20.glClearColor(0, 0, 0, 1);
                            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                            displayImageFilter.setTextureWH(filter.getTextureW(), filter.getTextureH());
                            displayImageFilter.onDrawFrame(i);
                            base.swapBuffers();

                            filter.destroy();
                            displayImageFilter.destroy();
                            base.releaseEglSurface();
                            eglCore.release();
                        }
                    }
                }).start();
            }
        }, 3000);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d("xxx", "onSurfaceTextureSizeChanged: surface == " + surface + " , width == " + width + " , height == " + height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d("xxx", "onSurfaceTextureDestroyed: surface == " + surface);
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Log.d("xxx", "onSurfaceTextureUpdated: surface == " + surface);
    }
}
