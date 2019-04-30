package lib.gl.filter.rhythm;

import android.content.Context;
import android.opengl.GLES20;
import lib.gl.filter.GLConstant;
import lib.gl.filter.GPUFilterType;
import lib.gl.filter.GPUImageFilter;
import lib.gl.util.ByteBufferUtil;
import lib.gl.util.GlMatrixTools;

/**
 * @author Gxx
 * Created by Gxx on 2019/4/08.
 */
public class DisplayFilter extends GPUImageFilter {

    public DisplayFilter(Context context) {
        super(context);
    }

    @Override
    protected boolean onInitTaskMgr() {
        return false;
    }

    @Override
    protected void onInitBufferData() {
        mVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.VERTEX_SQUARE);
        mVertexIndexBuffer = ByteBufferUtil.getNativeShortBuffer(GLConstant.VERTEX_INDEX);
        mTextureIndexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.TEXTURE_INDEX_V2);
    }

    @Override
    public GPUFilterType getFilterType() {
        return null;
    }

    @Override
    protected boolean needInitMsaaFbo() {
        return false;
    }

    @Override
    protected void preDrawSteps3Matrix(boolean drawBuffer) {
        if (!isViewPortAvailable(drawBuffer)) return;

        // 视口区域大小(归一化映射范围)
        GLES20.glViewport(0, 0, drawBuffer ? getFrameBufferW() : getSurfaceW(), drawBuffer ? getFrameBufferH() : getSurfaceH());
        // 矩阵变换
        GlMatrixTools matrix = getMatrix();
        matrix.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
        matrix.frustum(-1, 1, -1, 1, 3, 7);

        matrix.pushMatrix();
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }
}
