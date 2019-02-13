package lib.gl.filter.innovation;

import android.content.Context;
import android.opengl.GLES30;

import lib.gl.filter.GLConstant;
import lib.gl.filter.GPUFilterType;
import lib.gl.filter.GPUImageFilter;
import lib.gl.R;
import lib.gl.util.ByteBufferUtil;
import lib.gl.util.GLUtil;

/**
 * 简单模糊
 * @author Gxx
 * Created by Gxx on 2018/12/29.
 */
public class FuzzyFilter extends GPUImageFilter
{
    private int intensityHandle;
    private int passesHandle;

    public FuzzyFilter(Context context)
    {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default), GLUtil.readShaderFromRaw(context, R.raw.fragment_fuzzy));
    }

    @Override
    protected boolean onInitTaskMgr()
    {
        return false;
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
        super.onInitProgramHandle();

        intensityHandle = GLES30.glGetUniformLocation(getProgram(), "intensity");
    }

    @Override
    public GPUFilterType getFilterType()
    {
        return null;
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer)
    {
        GLES30.glUniform1f(intensityHandle, 0.236638f);
    }

    @Override
    public void onDrawFrame(int textureID)
    {
        GLES30.glClearColor(1,1,1,1);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        super.onDrawFrame(textureID);
    }
}
