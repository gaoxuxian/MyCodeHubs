package filter.transitions;

import android.content.Context;
import android.opengl.GLES20;

import filter.GPUFilterType;
import filter.GPUImageTransitionFilter;
import filter.GPUTransitionFilterType;
import library.R;
import util.GLUtil;
import util.GlMatrixTools;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/4.
 */
public class CircleCropTransitionFilter extends GPUImageTransitionFilter
{
    private int ratioHandle;
    private int bgColorHandle;

    private float[] bgColorValue;

    public CircleCropTransitionFilter(Context context)
    {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default), GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_circle_crop));
    }

    @Override
    protected boolean onInitTaskMgr()
    {
        return false;
    }

    @Override
    protected void onInitBaseData()
    {
        super.onInitBaseData();

        bgColorValue = new float[4];
        bgColorValue[0] = 0;
        bgColorValue[1] = 0;
        bgColorValue[2] = 0;
        bgColorValue[3] = 1;
    }

    @Override
    public GPUTransitionFilterType getFilterType()
    {
        return GPUTransitionFilterType.CIRCLE_CROP;
    }

    @Override
    protected void onInitProgramHandle()
    {
        super.onInitProgramHandle();

        ratioHandle = GLES20.glGetUniformLocation(getProgram(), "ratio");
        bgColorHandle = GLES20.glGetUniformLocation(getProgram(), "bgColor");
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

        GLES20.glUniform1f(ratioHandle, (float) mTextureW / mTextureH);
        GLES20.glUniform4fv(bgColorHandle, 1, bgColorValue, 0);
    }

    @Override
    protected float getEffectTimeCycle()
    {
        return 1200f;
    }

    @Override
    protected boolean isEffectCycle()
    {
        return false;
    }
}
