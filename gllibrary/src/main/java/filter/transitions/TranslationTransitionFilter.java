package filter.transitions;

import android.content.Context;
import android.opengl.GLES20;

import filter.GPUFilterType;
import filter.GPUImageTransitionFilter;
import library.R;
import util.GLUtil;
import util.GlMatrixTools;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/3.
 */
public class TranslationTransitionFilter extends GPUImageTransitionFilter
{
    private int directionHandle;
    private float[] directionValue; // 平移方向 -1\0\1

    public TranslationTransitionFilter(Context context)
    {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default), GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_translation));
    }

    @Override
    protected void onInitBaseData()
    {
        super.onInitBaseData();

        directionValue = new float[2];
        directionValue[0] = 1;
        directionValue[1] = 1;
    }

    @Override
    public GPUFilterType getFilterType()
    {
        return GPUFilterType.TRANSITION_TRANSLATION;
    }

    @Override
    protected void onInitProgramHandle()
    {
        super.onInitProgramHandle();

        directionHandle = GLES20.glGetUniformLocation(getProgram(), "direction");
    }

    @Override
    protected void preDrawSteps3Matrix(boolean drawBuffer)
    {
        if (!isViewPortAvailable(drawBuffer)) return;

        // 视口区域大小(归一化映射范围)
        GLES20.glViewport(0, 0, drawBuffer ? getFrameBufferW() : getSurfaceW(), drawBuffer ? getFrameBufferH() : getSurfaceH());
        // 矩阵变换
        GlMatrixTools matrix = getMatrix();
        matrix.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
        float vs = drawBuffer ? (float) getFrameBufferH() / getFrameBufferW() : (float) getSurfaceH() / getSurfaceW();
        matrix.frustum(-1, 1, -vs, vs, 3, 7);

        matrix.pushMatrix();
        matrix.scale(1f, (float) mTextureH / mTextureW, 1f);
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer)
    {
        super.preDrawSteps4Other(drawBuffer);

        GLES20.glUniform2fv(directionHandle, 1, directionValue, 0);
    }

    @Override
    public void setTimeValue(long time)
    {
        float dt = (time - mStartTime) / 2500f;
        int dtInt = (int) dt;
        mProgressValue = dt - dtInt;
        if (dtInt > 0)
        {
            mProgressValue = 1;
        }
    }
}
