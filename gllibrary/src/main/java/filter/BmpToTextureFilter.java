package filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import task.BmpToTextureTask;
import util.GLUtil;
import util.GlMatrixTools;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/21.
 */
public class BmpToTextureFilter extends GPUImageFilter
{
    private BmpToTextureTask mTask;
    private int mBmpW;
    private int mBmpH;
    private int[] mTextureIDs;

    public BmpToTextureFilter(Context context)
    {
        super(context);
    }

    @Override
    protected void onInitBaseData()
    {
        super.onInitBaseData();

        mTextureIDs = new int[1];

        mTask = new BmpToTextureTask(getContext(), new BmpToTextureTask.Listener()
        {
            @Override
            public void onBitmapSucceed(Bitmap bitmap)
            {
                if (bitmap != null && !bitmap.isRecycled())
                {
                    if (mBmpW != bitmap.getWidth() || mBmpH != bitmap.getHeight())
                    {
                        mBmpW = bitmap.getWidth();
                        mBmpH = bitmap.getHeight();

                        if (GLES30.glIsTexture(mTextureIDs[0]))
                        {
                            GLES30.glDeleteTextures(1, mTextureIDs, 0);
                            mTextureIDs[0] = 0;
                        }

                        GLES30.glGenTextures(1, mTextureIDs, 0);
                        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureIDs[0]);
                        GLUtil.bindTexture2DParams();
                        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
                        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
                    }
                    else if (!GLES30.glIsTexture(mTextureIDs[0]))
                    {
                        GLES30.glGenTextures(1, mTextureIDs, 0);
                        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureIDs[0]);
                        GLUtil.bindTexture2DParams();
                        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
                        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
                    }
                    else
                    {
                        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureIDs[0]);
                        GLUtil.bindTexture2DParams();
                        GLUtils.texSubImage2D(GLES30.GL_TEXTURE_2D, 0, 0, 0, bitmap);
                        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
                    }
                }
            }
        });
    }

    @Override
    protected boolean needInitMsaaFbo()
    {
        return false;
    }

    @Override
    public void initFrameBuffer(int width, int height)
    {
        if (mBmpW != 0 && mBmpH != 0)
        {
            if (mFrameBufferMgr == null)
            {
                super.initFrameBuffer(mBmpW, mBmpH, true, false, false);
            }
            else if (mFrameBufferMgr.getBufferWidth() != mBmpW || mFrameBufferMgr.getBufferHeight() != mBmpH)
            {
                checkFrameBufferReMount(mBmpW, mBmpH);
            }
        }
    }

    @Override
    protected void preDrawSteps3Matrix()
    {
        GlMatrixTools matrix = getMatrix();
        matrix.pushMatrix();
        matrix.scale(1f, 1f, 1f);
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }

    @Override
    protected void preDrawSteps4Other()
    {
        GLES30.glViewport(0, 0, mBmpW, mBmpH);
    }

    @Override
    public int onDrawBuffer(int textureID)
    {
        if (!GLES30.glIsProgram(getProgram()))
        {
            return textureID;
        }

        if (mFrameBufferMgr != null)
        {
            mFrameBufferMgr.bindNext();

            draw(mTextureIDs[0]);
            mFrameBufferMgr.unbind();
            return mFrameBufferMgr.getCurrentTextureId();
        }

        return textureID;
    }

    public void setBitmapRes(Object res)
    {
        if (mTask != null)
        {
            mBmpW = 0;
            mBmpH = 0;

            mTask.setBitmapRes(res);
            queueRunnable(mTask);
            runTask(true); // 卡线程
        }
    }

    @Override
    public GPUFilterType getFilterType()
    {
        return GPUFilterType.BITMAP_TRANSFORM_TEXTURE;
    }

    @Override
    public void destroy()
    {
        super.destroy();

        if (mTask != null)
        {
            mTask.destroy();
        }
    }
}
