package filter.common;

import android.content.Context;

import filter.GLConstant;
import filter.GPUFilterType;
import filter.GPUImageFilter;
import util.ByteBufferUtil;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/21.
 */
public class DisplayImageFilter extends GPUImageFilter
{
    public DisplayImageFilter(Context context)
    {
        super(context);
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
    public GPUFilterType getFilterType()
    {
        return GPUFilterType.DISPLAY;
    }
}