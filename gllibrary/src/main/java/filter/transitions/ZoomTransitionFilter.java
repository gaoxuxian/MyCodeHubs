package filter.transitions;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;

import filter.GPUFilterType;
import filter.GPUImageTransitionFilter;
import library.R;
import util.GLUtil;
import util.GlMatrixTools;

/**
 * 变焦转场
 * @author Gxx
 * Created by Gxx on 2018/12/29.
 */
public class ZoomTransitionFilter extends GPUImageTransitionFilter
{
    private int zoomQuicknessHandle;
    private int progressHandle;

    public ZoomTransitionFilter(Context context)
    {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default), GLUtil.readShaderFromRaw(context, R.raw.fragment_zoom_fuzzy_transition));
    }

    @Override
    protected boolean onInitTaskMgr()
    {
        return false;
    }

    @Override
    public GPUFilterType getFilterType()
    {
        return GPUFilterType.TRANSITION_ZOOM;
    }

    @Override
    protected void onInitProgramHandle()
    {
        super.onInitProgramHandle();

        zoomQuicknessHandle = GLES30.glGetUniformLocation(getProgram(), "zoom_quickness");
        progressHandle = GLES30.glGetUniformLocation(getProgram(), "progress");
    }

    @Override
    protected void preDrawSteps3Matrix()
    {
        GLES20.glViewport(0, 0, getSurfaceW(), getSurfaceH());

        GlMatrixTools matrix = getMatrix();
        float vs = (float) getSurfaceH() / getSurfaceW();
        matrix.frustum(-1, 1, -vs, vs, 3, 5);
        matrix.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
        matrix.pushMatrix();
        matrix.scale(1f, (float) mTextureH / mTextureW, 1f);
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }

    @Override
    protected void preDrawSteps4Other()
    {
        GLES20.glUniform1f(zoomQuicknessHandle, 0.6f);

        if (mStartTime == 0)
        {
            mStartTime = System.currentTimeMillis();
        }

        float dt = (System.currentTimeMillis() - mStartTime) / 2500f;
        int dtInt = (int) dt;
        float progress = dt - dtInt;
        if (dtInt > 0)
        {
            progress = 1;
        }
        GLES20.glUniform1f(progressHandle, progress);

        GLES20.glClearColor(1, 1, 1, 1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        blendEnable(true);
    }

    @Override
    protected void afterDraw()
    {
        super.afterDraw();
        blendEnable(false);
    }
}
