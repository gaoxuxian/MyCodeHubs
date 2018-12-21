package trunk.gles.view;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import filter.innovation.SplitScreenFilter;
import util.GLUtil;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/21.
 */
public class SplitScreenView extends GLSurfaceView implements GLSurfaceView.Renderer
{
    private SplitScreenFilter mFilter;

    public SplitScreenView(Context context)
    {
        super(context);

        mFilter = new SplitScreenFilter(context);

        setEGLContextClientVersion(GLUtil.getGlSupportVersionInt(context));
        setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        setRenderer(this);
        // setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        // requestRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        mFilter.onSurfaceCreated(config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        mFilter.onSurfaceChanged(width, height);
        mFilter.initFrameBuffer(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        mFilter.onDrawFrame(0);
    }
}
