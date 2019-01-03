package filter;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import library.R;
import util.ByteBufferUtil;
import util.GLUtil;
import util.GlMatrixTools;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/2.
 */
public abstract class GPUImageTransitionFilter extends AbsFilter
{
    protected int vPositionHandle;
    protected int vCoordinateHandle;
    protected int vMatrixHandle;
    protected int vTextureFrontHandle;
    protected int vTextureBackHandle;
    protected int progressHandle;

    protected float mProgressValue;
    protected long mStartTime;

    protected FloatBuffer mVertexBuffer;
    protected ShortBuffer mVertexIndexBuffer;
    protected FloatBuffer mTextureIndexBuffer;

    public GPUImageTransitionFilter(Context context)
    {
        this(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default), GLUtil.readShaderFromRaw(context, R.raw.fragment_image_transition_default));
    }

    public GPUImageTransitionFilter(Context context, String vertex, String fragment)
    {
        super(context, vertex, fragment);
    }

    @Override
    protected void onInitBufferData()
    {
        mVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.VERTEX_SQUARE);
        mVertexIndexBuffer = ByteBufferUtil.getNativeShortBuffer(GLConstant.VERTEX_INDEX);
        mTextureIndexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.TEXTURE_INDEX_V2);
    }

    @Override
    protected void onInitProgramHandle()
    {
        vPositionHandle = GLES20.glGetAttribLocation(getProgram(), "vPosition");
        vCoordinateHandle = GLES20.glGetAttribLocation(getProgram(), "vCoordinate");
        vMatrixHandle = GLES20.glGetUniformLocation(getProgram(), "vMatrix");
        vTextureFrontHandle = GLES20.glGetUniformLocation(getProgram(), "vTextureFront");
        vTextureBackHandle = GLES20.glGetUniformLocation(getProgram(), "vTextureBack");
        progressHandle = GLES20.glGetUniformLocation(getProgram(), "progress");
    }

    protected void preDrawSteps1DataBuffer()
    {
        // 绑定顶点坐标缓冲
        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(vPositionHandle, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(vPositionHandle);

        // 绑定纹理坐标缓冲
        mTextureIndexBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureIndexBuffer);
        GLES20.glEnableVertexAttribArray(vCoordinateHandle);
    }

    protected void preDrawSteps2BindTexture(int front, int back)
    {
        // 绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(getTextureType(), front);
        GLES20.glUniform1i(vTextureFrontHandle, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(getTextureType(), back);
        GLES20.glUniform1i(vTextureBackHandle, 1);
    }

    protected int getTextureType()
    {
        return GLES20.GL_TEXTURE_2D;
    }

    protected void preDrawSteps3Matrix()
    {
        GLES20.glViewport(0, 0, getSurfaceW(), getSurfaceH());

        // 矩阵变换
        GlMatrixTools matrix = getMatrix();
        matrix.pushMatrix();
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }

    protected void preDrawSteps4Other()
    {
        GLES20.glUniform1f(progressHandle, mProgressValue);
    }

    protected final void draw(int front, int back)
    {
        GLES20.glUseProgram(getProgram());

        preDrawSteps1DataBuffer();
        preDrawSteps2BindTexture(front, back);
        preDrawSteps3Matrix();
        preDrawSteps4Other();

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);

        afterDraw();
    }

    protected void afterDraw()
    {
        GLES20.glDisableVertexAttribArray(vPositionHandle);
        GLES20.glDisableVertexAttribArray(vCoordinateHandle);
        GLES20.glBindTexture(getTextureType(), 0);
    }

    public void onDrawFrame(int frontTextureID, int backTextureID)
    {
        if (!GLES20.glIsProgram(getProgram()))
        {
            return;
        }
        draw(frontTextureID, backTextureID);
    }

    public int onDrawBuffer(int lastTextureID, int frontTextureID, int backTextureID)
    {
        if (!GLES20.glIsProgram(getProgram()))
        {
            return lastTextureID;
        }

        if (mFrameBufferMgr != null)
        {
            mFrameBufferMgr.bindNext();
            mFrameBufferMgr.clearColor(true, true, true, true, true);
            mFrameBufferMgr.clearDepth(true, true);
            mFrameBufferMgr.clearStencil(true, true);

            draw(frontTextureID, backTextureID);
            mFrameBufferMgr.unbind();
            return mFrameBufferMgr.getCurrentTextureId();
        }

        return lastTextureID;
    }

    public void setStartTimeValue(long start)
    {
        mStartTime = start;
    }

    public void setTimeValue(long time)
    {
        float dt = (time - mStartTime) / 1200f;
        int dtInt = (int) dt;
        mProgressValue = dt - dtInt;
        if (dtInt > 0)
        {
            mProgressValue = 1;
        }
    }
}
