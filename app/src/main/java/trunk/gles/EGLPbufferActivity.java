package trunk.gles;

import egl.ComponentSizeChooser;
import egl.ComponentSizeChooser14;
import egl.EGLMgr;
import egl.EGLMgr10;
import egl.EGLMgr14;
import filter.common.BmpToTextureFilter;
import trunk.BaseActivity;
import trunk.R;
import util.GLUtil;
import util.PxUtil;
import util.ThreadUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.os.Build;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.nio.IntBuffer;

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

            EGLMgr eglMgr = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            {
                EGLMgr14 mgr = new EGLMgr14();
                mgr.setUpEglBackgroundEnvironment(glesVersion, new ComponentSizeChooser14(glesVersion, 8, 8, 8, 8, 0, 0), width, height);
                eglMgr = mgr;
            }
            else
            {
                EGLMgr10 mgr = new EGLMgr10();
                mgr.setUpEglBackgroundEnvironment(glesVersion, new ComponentSizeChooser(glesVersion, 8, 8, 8, 8, 0, 0), width, height);
                eglMgr = mgr;
            }

            BmpToTextureFilter filter = new BmpToTextureFilter(ctx);
            filter.onSurfaceCreated(null);
            filter.onSurfaceChanged(width, height);
            filter.setBitmapRes(bitmap);
            filter.onDrawFrame(0);

            int[] iat = new int[width * height];
            IntBuffer byteBuffer = IntBuffer.allocate(width * height);
            GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, byteBuffer);

            int[] array = byteBuffer.array();

            for (int i = 0; i < height; i++)
            {
                System.arraycopy(array, i * width, iat, (height - i - 1) * width, width);
            }

            Bitmap out = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            out.copyPixelsFromBuffer(IntBuffer.wrap(iat));

            filter.destroy();
            eglMgr.destroy();

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
