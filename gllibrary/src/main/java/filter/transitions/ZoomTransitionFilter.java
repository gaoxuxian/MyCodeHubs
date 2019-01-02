package filter.transitions;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;

import filter.GLConstant;
import filter.GPUFilterType;
import filter.GPUImageFilter;
import library.R;
import util.ByteBufferUtil;
import util.GLUtil;
import util.GlMatrixTools;

/**
 * 变焦转场
 * @author Gxx
 * Created by Gxx on 2018/12/29.
 */
public class ZoomTransitionFilter extends GPUImageFilter
{
    private int[] mTextureIDs; // 0 -- front, 1 -- back
    private int mTextureW;
    private int mTextureH;

    private int vTextureFrontHandle;
    private int vTextureBackHandle;
    private int zoomQuicknessHandle;
    private int progressHandle;

    private long mStartTime;

    public ZoomTransitionFilter(Context context)
    {
        super(context, GLUtil.readShaderFromRaw(context, R.raw.vertex_image_default), GLUtil.readShaderFromRaw(context, R.raw.fragment_zoom_fuzzy_transition));
    }

    @Override
    protected boolean onInitTaskMgr()
    {
        return false;
    }

    @Override
    public GPUFilterType getFilterType()
    {
        return GPUFilterType.TRANSITION_ZOOM;
    }

    @Override
    protected void onInitBaseData()
    {
        mTextureIDs = new int[2];
    }

    @Override
    protected void onInitProgramHandle()
    {
        vPositionHandle = GLES20.glGetAttribLocation(getProgram(), "vPosition");
        vCoordinateHandle = GLES20.glGetAttribLocation(getProgram(), "vCoordinate");
        vMatrixHandle = GLES20.glGetUniformLocation(getProgram(), "vMatrix");
        vTextureFrontHandle = GLES30.glGetUniformLocation(getProgram(), "vTextureFront");
        vTextureBackHandle = GLES30.glGetUniformLocation(getProgram(), "vTextureBack");
        zoomQuicknessHandle = GLES30.glGetUniformLocation(getProgram(), "zoom_quickness");
        progressHandle = GLES30.glGetUniformLocation(getProgram(), "progress");
    }

    @Override
    protected void onInitBufferData()
    {
        mVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.VERTEX_SQUARE);
        mVertexIndexBuffer = ByteBufferUtil.getNativeShortBuffer(GLConstant.VERTEX_INDEX);
        mTextureIndexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.TEXTURE_INDEX_V2);
    }

    @Override
    protected void preDrawSteps2BindTexture(int textureID)
    {
        // 绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(getTextureType(), mTextureIDs[0]);
        GLES20.glUniform1i(vTextureFrontHandle, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(getTextureType(), mTextureIDs[1]);
        GLES20.glUniform1i(vTextureBackHandle, 1);
    }

    @Override
    protected void preDrawSteps3Matrix()
    {
        GlMatrixTools matrix = getMatrix();
        float vs = (float) getSurfaceH() / getSurfaceW();
        matrix.frustum(-1, 1, -vs, vs, 3, 5);
        matrix.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
        matrix.pushMatrix();
        matrix.scale(1f, (float) mTextureH / mTextureW, 1f);
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }

    @Override
    protected void preDrawSteps4Other()
    {
        GLES20.glUniform1f(zoomQuicknessHandle, 0.6f);

        if (mStartTime == 0)
        {
            mStartTime = System.currentTimeMillis();
        }

        float dt = (System.currentTimeMillis() - mStartTime) / 2500f;
        int dtInt = (int) dt;
        float progress = dt - dtInt;
        if (dtInt > 0)
        {
            progress = 1;
        }
        GLES20.glUniform1f(progressHandle, progress);

        GLES20.glClearColor(1, 1, 1, 1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        blendEnable(true);
    }

    @Override
    protected void afterDraw()
    {
        super.afterDraw();
        blendEnable(false);
    }

    public void setTextureID(int front, int back, int width, int height)
    {
        mTextureIDs[0] = front;
        mTextureIDs[1] = back;
        mTextureW = width;
        mTextureH = height;
    }
}
