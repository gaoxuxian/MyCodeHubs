package lib.gl.fbo;

import android.content.Context;
import lib.gl.util.GLUtil;


/**
 * @author Gxx
 * Created by Gxx on 2018/12/20.
 */
public class FrameBufferMgr extends AbsFboMgr
{
    private AbsFboMgr mMgr;

    public FrameBufferMgr(Context context, int width, int height, int size, boolean color, boolean depth, boolean stencil, boolean msaa)
    {
        super(width, height, size, color, depth, stencil);
        init(context, width, height, mBufferSize, color, depth, stencil, msaa);
    }

    @Override
    protected void init(int width, int height, int size, boolean color, boolean depth, boolean stencil)
    {

    }

    private void init(Context context, int width, int height, int size, boolean color, boolean depth, boolean stencil, boolean msaa)
    {
        if (GLUtil.checkSupportGlVersion(context, 3.0f))
        {
            if (msaa)
            {
                mMgr = new MsaaFboMgr(width, height, size, depth, stencil);
            }
            else
            {
                mMgr = new TextureFboMgr30(width, height, size, color, depth, stencil);
            }
        }
        else
        {
            mMgr = new TextureFboMgr20(width, height, size, color, depth, stencil);
        }
    }

    @Override
    public void reMount(int width, int height)
    {
        if (mMgr != null)
        {
            mMgr.reMount(width, height);
        }
    }

    @Override
    public int getBufferHeight()
    {
        return mMgr != null ? mMgr.getBufferHeight() : 0;
    }

    @Override
    public int getBufferWidth()
    {
        return mMgr != null ? mMgr.getBufferWidth() : 0;
    }

    @Override
    public boolean bindNext()
    {
        return mMgr != null ? mMgr.bindNext() : super.bindNext();
    }

    @Override
    public boolean bindNext(int textureID)
    {
        return mMgr != null && mMgr.bindNext(textureID);
    }

    @Override
    public int getCurrentTextureId()
    {
        return mMgr != null ? mMgr.getCurrentTextureId() : 0;
    }

    @Override
    public int getPreviousTextureId()
    {
        return mMgr != null ? mMgr.getPreviousTextureId() : 0;
    }

    @Override
    public void destroy()
    {
        if (mMgr != null)
        {
            mMgr.destroy();
            mMgr = null;
        }
    }

    @Override
    public boolean isAvailable() {
        return mMgr != null;
    }
}
