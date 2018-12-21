package filter.innovation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import filter.GPUImageFilter;
import library.R;
import util.GLUtil;
import util.GlMatrixTools;

/**
 * @author Gxx 分屏
 * Created by Gxx on 2018/12/21.
 */
public class SplitScreenFilter extends GPUImageFilter
{
    private boolean mDrawFrame;
    private boolean mDrawBgFBO;
    private boolean mDrawContent;
    private boolean mDrawContentFBO;

    private int[] mTextureIDs;
    private int vFactorHandle;

    private long mTime;
    private int vBHandle;

    public SplitScreenFilter(Context context)
    {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_split_screen), GLUtil.readShaderFromRaw(context, R.raw.fragment_spilt_screen));
    }

    @Override
    protected void onInitBaseData()
    {
        mTextureIDs = new int[2];
    }

    @Override
    protected void onInitProgramHandle()
    {
        super.onInitProgramHandle();
        vFactorHandle = GLES30.glGetUniformLocation(getProgram(), "vFactor");
    }

    @Override
    protected int createFrameBufferSize()
    {
        return 2;
    }

    @Override
    protected boolean needInitMsaaFbo()
    {
        return false;
    }

    @Override
    protected void preDrawSteps3Matrix()
    {
        GlMatrixTools matrix = getMatrix();
        matrix.pushMatrix();
        matrix.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
        float vs = (float) getSurfaceH() / (float) getSurfaceW();
        matrix.frustum(-1, 1, -vs, vs, 3, 7);
        if (mDrawFrame || mDrawContent)
        {
            matrix.scale(1f, -1f, 1f);
        }
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }

    @Override
    protected void preDrawSteps4Other()
    {
        if (mDrawBgFBO)
        {
            if (!GLES30.glIsTexture(mTextureIDs[0]))
            {
                GLES30.glGenTextures(1, mTextureIDs, 0);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureIDs[0]);
                GLUtil.bindTexture2DParams();
                Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.open_test_2);
                GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
            }

            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureIDs[0]);
            GLES30.glUniform1i(vTextureHandle, 0);

            GLES30.glUniform1f(vFactorHandle, 5);
        }
        else if (mDrawContentFBO)
        {
            if (!GLES30.glIsTexture(mTextureIDs[1]))
            {
                GLES30.glGenTextures(1, mTextureIDs, 1);
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureIDs[1]);
                GLUtil.bindTexture2DParams();
                Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.open_test_5);
                GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
            }

            GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureIDs[1]);
            GLES30.glUniform1i(vTextureHandle, 0);

            GLES30.glUniform1f(vFactorHandle, 5);
        }
        else if (mDrawContent)
        {
            GLES30.glUniform1f(vFactorHandle, 0);

            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
            GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);
        }
        else if (mDrawFrame)
        {
            GLES30.glUniform1f(vFactorHandle, 5);
        }
    }

    @Override
    protected void afterDraw()
    {
        super.afterDraw();

        if (mDrawContent)
        {
            blendEnable(false);
        }
    }

    @Override
    public void onDrawFrame(int textureID)
    {
        if (!GLES20.glIsProgram(getProgram()))
        {
            return;
        }

        if (mFrameBufferMgr != null)
        {
            mFrameBufferMgr.bindNext();
        }

        mDrawBgFBO = true;
        draw(textureID);
        mDrawBgFBO = false;

        int bgId = 0;

        if (mFrameBufferMgr != null)
        {
            bgId = mFrameBufferMgr.getCurrentTextureId();
            mFrameBufferMgr.bindNext();
        }

        mDrawContentFBO = true;
        draw(textureID);
        mDrawContentFBO = false;

        int contentID = 0;

        if (mFrameBufferMgr != null)
        {
            contentID = mFrameBufferMgr.getCurrentTextureId();
            mFrameBufferMgr.bindNext(bgId);
        }

        mDrawContent = true;
        draw(contentID);
        mDrawContent = false;

        if (mFrameBufferMgr != null)
        {
            mFrameBufferMgr.unbind();
        }

        mDrawFrame = true;
        draw(bgId);
        mDrawFrame = false;
    }
}
