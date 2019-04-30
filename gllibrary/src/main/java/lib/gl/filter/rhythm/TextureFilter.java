package lib.gl.filter.rhythm;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import androidx.annotation.FloatRange;
import lib.gl.R;
import lib.gl.filter.GPUFilterType;
import lib.gl.filter.GPUImageFilter;
import lib.gl.util.GLUtil;
import lib.gl.util.GlMatrixTools;

import java.nio.ByteBuffer;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/23.
 */
public class TextureFilter extends GPUImageFilter {
    private TextureResTask mTask;
    private int[] mTextureIds;

    public TextureFilter(Context context) {
        this(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default), GLUtil.readShaderFromRaw(context, R.raw.fragment_image_default));
    }

    public TextureFilter(Context context, String vertex, String fragment) {
        super(context, vertex, fragment);
    }

    @Override
    protected void onInitBaseData() {
        super.onInitBaseData();

        mTextureIds = new int[1];

        mTask = new TextureResTask(getContext(), new TextureResTask.Listener() {
            @Override
            public void onStart(int width, int height) {
                if (getTextureW() != width || getTextureH() != height) {
                    setTextureWH(width, height);

                    if (GLES20.glIsTexture(mTextureIds[0])) {
                        GLES20.glDeleteTextures(1, mTextureIds, 0);
                        mTextureIds[0] = GLES20.GL_NONE;
                    }
                }
            }

            @Override
            public void onImageSucceed(Bitmap bitmap) {
                if (GLES20.glIsTexture(mTextureIds[0])) {
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureIds[0]);
                    GLUtil.bindTexture2DParams();
                    GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, bitmap);
                } else {
                    GLES20.glGenTextures(1, mTextureIds, 0);
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureIds[0]);
                    GLUtil.bindTexture2DParams();
                    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
                }
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            }

            @Override
            public void onImageSucceed(ByteBuffer bitmap) {
                if (GLES20.glIsTexture(mTextureIds[0])) {
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureIds[0]);
                    GLUtil.bindTexture2DParams();
                    GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, getTextureW(), getTextureH(), GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, bitmap);
                } else {
                    GLES20.glGenTextures(1, mTextureIds, 0);
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureIds[0]);
                    GLUtil.bindTexture2DParams();
                    GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, getTextureW(), getTextureH(), 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, bitmap);
                }
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            }
        });
    }

    @Override
    protected void onInitBufferData() {
        super.onInitBufferData();
    }

    @Override
    protected void preDrawSteps3Matrix(boolean drawBuffer) {
        if (!isViewPortAvailable(drawBuffer)) return;

        // 视口区域大小(归一化映射范围)
        GLES20.glViewport(0, 0, drawBuffer ? getFrameBufferW() : getSurfaceW(), drawBuffer ? getFrameBufferH() : getSurfaceH());
        // 矩阵变换
        GlMatrixTools matrix = getMatrix();
        matrix.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);

        float vs = drawBuffer ? (float) getFrameBufferH() / getFrameBufferW() : (float) getSurfaceH() / getSurfaceW();
        matrix.frustum(-1, 1, -vs, vs, 3, 7);

        matrix.pushMatrix();
        float x_scale = 1f;
        float y_scale = getTextureH() != 0 && getTextureW() != 0 ? (float) getTextureH() / getTextureW() : 1f;
        float scale = mScaleFullIn ? Math.max(1f, vs / y_scale) : Math.min(1f, vs / y_scale);
        matrix.scale(x_scale * scale, y_scale * scale, 1f);
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer) {
        super.preDrawSteps4Other(drawBuffer);
    }

    /**
     * 先通过 createGlTexture() 生成纹理, 再绘制正确方向
     *
     * @param textureID
     * @return
     */
    @Override
    public int onDrawBuffer(int textureID) {
        return super.onDrawBuffer(textureID);
    }

    /**
     * 先通过 createGlTexture() 生成纹理, 再绘制正确方向
     *
     * @param textureID
     */
    @Override
    public void onDrawFrame(int textureID) {
        super.onDrawFrame(textureID);
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
    public void destroy() {
        super.destroy();

        if (mTask != null) {
            mTask.destroy();
        }

        if (mTextureIds != null && mTextureIds.length > 0) {
            if (GLES20.glIsTexture(mTextureIds[0])) {
                GLES20.glDeleteTextures(1, mTextureIds, 0);
                mTextureIds[0] = GLES20.GL_NONE;
            }
        }
    }

    /**
     * GL环境下, 卡线程完成纹理构建工作, 纹理方向可能是y轴翻转
     *
     * @param imageRes bitmap \ res id \ byte[] 等数据
     * @return GL texture id, if imageRes is out of form, return GL_NONE
     */
    public int createGlTexture(Object imageRes) {
        if (mTask != null) {
            mTask.setImageResource(imageRes);
            queueRunnable(mTask);
            runTask(true);
        }
        return mTextureIds[0];
    }

    // 记录是否铺满显示
    private boolean mScaleFullIn;

    /**
     * 设置图片缩放方式
     *
     * @param fullIn true-铺满(最短边顶边适配)，false-居中(最长边顶边适配)
     */
    public void setScaleFullIn(boolean fullIn) {
        mScaleFullIn = fullIn;
    }
}
