package filter.transitions;

import android.content.Context;
import android.opengl.GLES20;

import filter.GPUFilterType;
import filter.GPUImageTransitionFilter;
import library.R;
import util.GLUtil;
import util.GlMatrixTools;

/**
 * 方块自身翻转
 * @author Gxx
 * Created by Gxx on 2019/1/8.
 */
public class SquareAnimTransitionFilter extends GPUImageTransitionFilter
{
    private int sizeHandle;
    private int[] sizeValue;

    public SquareAnimTransitionFilter(Context context)
    {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default), GLUtil.readShaderFromRaw(context, R.raw.fragment_transition_square_anim));
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

        sizeValue = new int[2];
        sizeValue[0] = 4;
        sizeValue[1] = 4;
    }

    @Override
    public GPUFilterType getFilterType()
    {
        return GPUFilterType.TRANSITION_SQUARE_ANIM;
    }

    @Override
    protected void onInitProgramHandle()
    {
        super.onInitProgramHandle();

        sizeHandle = GLES20.glGetUniformLocation(getProgram(), "size");
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

        GLES20.glUniform2iv(sizeHandle, 1, sizeValue, 0);
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
