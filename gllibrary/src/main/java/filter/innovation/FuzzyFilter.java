package filter.innovation;

import android.content.Context;
import android.opengl.GLES30;

import filter.GLConstant;
import filter.GPUFilterType;
import filter.GPUImageFilter;
import library.R;
import util.ByteBufferUtil;
import util.GLUtil;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/29.
 */
public class FuzzyFilter extends GPUImageFilter
{
    private int intensityHandle;
    private int progressHandle;

    private float progressValue;
    private long mStartTime;
    private int passesHandle;

    public FuzzyFilter(Context context)
    {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default), GLUtil.readShaderFromRaw(context, R.raw.fragment_fuzzy));
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
        progressHandle = GLES30.glGetUniformLocation(getProgram(), "progress");
        passesHandle = GLES30.glGetUniformLocation(getProgram(), "passes");
    }

    @Override
    public GPUFilterType getFilterType()
    {
        return null;
    }

    @Override
    protected void preDrawSteps3Matrix()
    {
        super.preDrawSteps3Matrix();
    }

    @Override
    protected void preDrawSteps4Other()
    {
        GLES30.glUniform1f(intensityHandle, 0.6f);

        if (mStartTime == 0)
        {
            mStartTime = System.currentTimeMillis();
        }

        float dt = (System.currentTimeMillis() - mStartTime) / 2000f;
        int dtInt = (int) dt;
        progressValue = dt - dtInt;

        GLES30.glUniform1f(progressHandle, progressValue);
        // if (dtInt > 0)
        // {
        //     GLES30.glUniform1f(progressHandle, 0);
        // }
        GLES30.glUniform1i(passesHandle, 6);
    }

    @Override
    public void onDrawFrame(int textureID)
    {
        GLES30.glClearColor(1,1,1,1);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        super.onDrawFrame(textureID);
    }
}
