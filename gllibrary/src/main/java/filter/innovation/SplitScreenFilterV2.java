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
 * @author Gxx 四角分屏
 * Created by Gxx on 2018/12/21.
 */
public class SplitScreenFilterV2 extends GPUImageFilter
{
    private int[] mTexutreIDs;
    private int vTexture2Handle;
    private int vTexture3Handle;
    private int vTexture4Handle;
    private int vTexture5Handle;

    public SplitScreenFilterV2(Context context)
    {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_split_screen_4), GLUtil.readShaderFromRaw(context, R.raw.fragment_split_screen_4));
    }

    @Override
    protected boolean onInitTaskMgr()
    {
        return false;
    }

    public void setTextures(int[] id)
    {
        mTexutreIDs = id;
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

        GLUtil.checkGlError("AAA");

        vTexture2Handle = GLES30.glGetUniformLocation(getProgram(), "vTexture2");
        vTexture3Handle = GLES30.glGetUniformLocation(getProgram(), "vTexture3");
        vTexture4Handle = GLES30.glGetUniformLocation(getProgram(), "vTexture4");
        vTexture5Handle = GLES30.glGetUniformLocation(getProgram(), "vTexture5");

        GLUtil.checkGlError("AAA");
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer)
    {
        if (mTexutreIDs != null)
        {
            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTexutreIDs[0]);
            GLES30.glUniform1i(vTextureHandle, 0);

            GLUtil.checkGlError("AAA");

            GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTexutreIDs[1]);
            GLES30.glUniform1i(vTexture2Handle, 1);

            GLUtil.checkGlError("AAA");

            GLES30.glActiveTexture(GLES30.GL_TEXTURE2);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTexutreIDs[2]);
            GLES30.glUniform1i(vTexture3Handle, 2);

            GLUtil.checkGlError("AAA");

            GLES30.glActiveTexture(GLES30.GL_TEXTURE3);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTexutreIDs[3]);
            GLES30.glUniform1i(vTexture4Handle, 3);

            GLUtil.checkGlError("AAA");

            GLES30.glActiveTexture(GLES30.GL_TEXTURE4);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTexutreIDs[4]);
            GLES30.glUniform1i(vTexture5Handle, 4);
        }
    }

    @Override
    public void onDrawFrame(int textureID)
    {
        if (!GLES30.glIsProgram(getProgram()))
        {
            return;
        }
        GLUtil.checkGlError("AAA");
        draw(textureID, false);
        GLUtil.checkGlError("AAA");
    }

    @Override
    public GPUFilterType getFilterType()
    {
        return null;
    }
}
