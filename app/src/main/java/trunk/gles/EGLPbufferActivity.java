package trunk.gles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.xx.avlibrary.gl.util.GLUtil;
import com.xx.avlibrary.gl.egl.EglCore;
import com.xx.avlibrary.gl.egl.EglSurfaceBase;
import com.xx.avlibrary.gl.filter.common.BmpToTextureFilter;
import com.xx.commonlib.PxUtil;
import com.xx.commonlib.ThreadUtil;

import trunk.BaseActivity;
import trunk.R;

public class EGLPbufferActivity extends BaseActivity
{
    private ImageView mImageView;

    @Override
    public void onCreateBaseData() throws Exception
    {

    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mImageView = new ImageView(context);
        mImageView.setBackgroundColor(Color.BLACK);
        params = new FrameLayout.LayoutParams(PxUtil.sU_1080p(1080), PxUtil.sU_1080p(1080));
        params.gravity = Gravity.CENTER;
        parent.addView(mImageView, params);

        Button btn = new Button(context);
        btn.setText("开始后台绘图");
        btn.setOnClickListener(v -> new Thread(() ->
        {
            Context ctx = v.getContext();

            int glesVersion = GLUtil.getGlSupportVersionInt(ctx);

            Bitmap bitmap = BitmapFactory.decodeResource(v.getResources(), R.drawable.open_test_4);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            EglCore egl = new EglCore();
            egl.setConfig(ctx, 8, 8, 8, 8, 0, 0, false);
            egl.initEglContext(null);
            EglSurfaceBase eglSurfaceBase = new EglSurfaceBase(egl);
            eglSurfaceBase.createPbufferSurface(width, height);
            eglSurfaceBase.makeCurrent();

            BmpToTextureFilter filter = new BmpToTextureFilter(ctx);
            filter.onSurfaceCreated(null);
            filter.onSurfaceChanged(width, height);
            filter.setBitmapRes(bitmap);
            filter.onDrawFrame(0);

            Bitmap out = eglSurfaceBase.getOutputBitmapFromFrame();

            filter.destroy();
            eglSurfaceBase.releaseEglSurface();
            egl.release();

            ThreadUtil.runOnUiThread(() ->
            {
                if (mImageView != null)
                {
                    mImageView.setImageBitmap(out);
                }
            });
        }).start());
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.addView(btn, params);
    }

    @Override
    public void onCreateFinish()
    {

    }
}
