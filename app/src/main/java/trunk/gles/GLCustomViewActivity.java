package trunk.gles;

import android.content.Context;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import lib.gl.view.MyRenderView;
import trunk.BaseActivity;

public class GLCustomViewActivity extends BaseActivity {

    private MyRenderView view;

    @Override
    public void onCreateBaseData() throws Exception {

    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params) {
        view = new MyRenderView(context);
        Log.d("xxx", "onCreateChildren: MyRenderView getHolder().getSurface() == " + view.getHolder().getSurface());
        view.setPreserveEGLContextOnPause(true);
        view.setRenderer(new MyRenderView.Renderer() {
            long firstTime;

            @Override
            public void onSurfaceCreated(EGLConfig config) {

            }

            @Override
            public void onSurfaceChanged(int width, int height) {

            }

            @Override
            public void onDrawFrame() {
                GLES20.glViewport(0, 0, 720, 1440);

                if (firstTime == 0) {
                    firstTime = System.currentTimeMillis();
                }

                float l = (System.currentTimeMillis() - firstTime) / 1000f;
                float dt = l - (int) l;

                GLES20.glClearColor(0.5f, 1*dt, 0, 1);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            }
        });
        view.setRenderMode(MyRenderView.RENDERMODE_WHEN_DIRTY);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(view, params);
    }

    @Override
    public void onCreateFinish() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        view.onResume();

        Log.d("xxx", "onResume: MyRenderView getHolder().getSurface() == " + view.getHolder().getSurface());
    }

    @Override
    protected void onPause() {
        super.onPause();

        view.onPause();
    }
}
