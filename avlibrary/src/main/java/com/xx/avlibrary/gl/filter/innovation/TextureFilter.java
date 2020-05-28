//package com.adnonstop.videotemplatelibs.gles.filter.common;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.opengl.GLES20;
//import android.opengl.GLUtils;
//import android.util.SparseArray;
//
//import com.adnonstop.videolibs.R;
//import com.adnonstop.videotemplatelibs.gles.filter.AbsFilter;
//import com.adnonstop.videotemplatelibs.gles.filter.common.task.TextureTask;
//import com.adnonstop.videotemplatelibs.gles.filter.effect.FilterType;
//import com.adnonstop.videotemplatelibs.gles.util.ByteBufferUtil;
//import com.adnonstop.videotemplatelibs.gles.util.GLConstant;
//import com.adnonstop.videotemplatelibs.gles.util.GLTextureUtil;
//import com.adnonstop.videotemplatelibs.gles.util.GLUtil;
//import com.adnonstop.videotemplatelibs.gles.util.GlMatrixTools;
//
//import java.nio.ByteBuffer;
//import java.nio.FloatBuffer;
//import java.nio.ShortBuffer;
//
///**
// * @author Gxx
// * Created by Gxx on 2019/1/23.
// */
//public class TextureFilter extends AbsFilter {
//    // 句柄
//    protected int vPositionHandle;
//    protected int vCoordinateHandle;
//    protected int vMatrixHandle;
//
//    protected FloatBuffer mVertexBuffer;
//    protected ShortBuffer mVertexIndexBuffer;
//    protected FloatBuffer mTextureIndexBuffer;
//
//    private int vTextureXHandle;
//    private int vTextureYHandle;
//    private int vTextureZHandle;
//    private int vFormatHandle;
//
//    private final int texture_index_x = 0;
//    private final int texture_index_y = 1;
//    private final int texture_index_z = 2;
//
//    private TextureTask mTask;
//    private int[] mTextureIds;
//    private Format mFormat = Format.RGBA;
//    private SparseArray<Format> mFormatArr;
//
//    public TextureFilter(Context context) {
//        this(context,
//                GLUtil.readShaderFromRaw(context, R.raw.vertex_image_texture),
//                GLUtil.readShaderFromRaw(context, R.raw.fragment_image_texture));
//    }
//
//    public TextureFilter(Context context, String vertex, String fragment) {
//        super(context, vertex, fragment);
//    }
//
//    @Override
//    protected boolean needInitMsaaFbo() {
//        return false;
//    }
//
//    protected int getTextureType() {
//        return GLES20.GL_TEXTURE_2D;
//    }
//
//    @Override
//    public FilterType getFilterType() {
//        return null;
//    }
//
//    @Override
//    protected void onInitBufferData() {
//        mVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.VERTEX_SQUARE);
//        mVertexIndexBuffer = ByteBufferUtil.getNativeShortBuffer(GLConstant.VERTEX_INDEX);
//        mTextureIndexBuffer = ByteBufferUtil.getNativeFloatBuffer(GLConstant.TEXTURE_INDEX);
//    }
//
//    @Override
//    protected void onInitProgramHandle() {
//        vPositionHandle = GLES20.glGetAttribLocation(getProgram(), "vPosition");
//        vCoordinateHandle = GLES20.glGetAttribLocation(getProgram(), "vCoordinate");
//        vMatrixHandle = GLES20.glGetUniformLocation(getProgram(), "vMatrix");
//        vTextureXHandle = GLES20.glGetUniformLocation(getProgram(), "vTextureX");
//        vTextureYHandle = GLES20.glGetUniformLocation(getProgram(), "vTextureY");
//        vTextureZHandle = GLES20.glGetUniformLocation(getProgram(), "vTextureZ");
//        vFormatHandle = GLES20.glGetUniformLocation(getProgram(), "vFormat");
//    }
//
//    private int mTempTextureW;
//    private int mTempTextureH;
//
//    private void setTempTextureWH(int width, int height) {
//        mTempTextureW = width;
//        mTempTextureH = height;
//    }
//
//    private int getTempTextureW() {
//        return mTempTextureW;
//    }
//
//    private int getTempTextureH() {
//        return mTempTextureH;
//    }
//
//    @Override
//    protected void onInitBaseData() {
//        super.onInitBaseData();
//
//        // 接收 YUV、RGBA、Bitmap 格式
//        mTextureIds = new int[3];
//        mFormatArr = new SparseArray<>();
//        mFormatArr.put(texture_index_x, Format.IDLE);
//        mFormatArr.put(texture_index_y, Format.IDLE);
//        mFormatArr.put(texture_index_z, Format.IDLE);
//
//        mTask = new TextureTask(getContext(), new TextureTask.Listener() {
//            @Override
//            public void onStart(int width, int height, int size, Format format) {
//                int formatArrSize = mFormatArr.size();
//                boolean resetWH = (getTempTextureW() != width || getTempTextureH() != height);
//                for (int i = 0; i < formatArrSize; i++) {
//                    Format oldFormat = mFormatArr.get(i);
//                    if (oldFormat != format || resetWH) {
//                        if (i < size) {
//                            deleteTexture(i);
//                            mFormatArr.put(i, format);
//                        } else {
//                            mFormatArr.put(i, Format.IDLE);
//                        }
//                    }
//                }
//                setTempTextureWH(width, height);
//                float degree = Math.abs(mDegree) % 360f;
//                if (degree == 90 || degree == 270) {
//                    width = width + height;
//                    height = width - height;
//                    width = width - height;
//                }
////                if (mAutoInitFbo) {
////                    int min = Math.min(width, height);
////                    if ((min * .8f + .5f) > 540) {
////                        width = (int) (width * .8f + .5f);
////                        height = (int) (height * .8f + .5f);
////                    }
////                }
//                setTextureWH(width, height);
//                if (mAutoInitFbo) {
//                    initFrameBufferOfTextureSize();
//                }
//            }
//
//            @Override
//            public void onImageSucceed(Bitmap bitmap) {
//                mFormat = Format.RGBA;
//                updateTexture(bitmap);
//                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//            }
//
//            @Override
//            public void onBufferSucceed(ByteBuffer rgba) {
//                mFormat = Format.RGBA;
//                int w = getTempTextureW();
//                int h = getTempTextureH();
//                updateTexture(texture_index_x, rgba, w, h, GLES20.GL_RGBA);
//                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//            }
//
//            @Override
//            public void onBufferSucceed(ByteBuffer y, ByteBuffer uv) {
//                mFormat = Format.NV12;
//                int w = getTempTextureW();
//                int h = getTempTextureH();
//                updateTexture(texture_index_x, y, w, h, GLES20.GL_LUMINANCE);
//                updateTexture(texture_index_y, uv, w / 2, h / 2, GLES20.GL_LUMINANCE_ALPHA);
//                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//            }
//
//            @Override
//            public void onBufferSucceed(ByteBuffer y, ByteBuffer u, ByteBuffer v) {
//                mFormat = Format.I420;
//                int w = getTempTextureW();
//                int h = getTempTextureH();
//                updateTexture(texture_index_x, y, w, h, GLES20.GL_LUMINANCE);
//                updateTexture(texture_index_y, u, w / 2, h / 2, GLES20.GL_LUMINANCE);
//                updateTexture(texture_index_z, v, w / 2, h / 2, GLES20.GL_LUMINANCE);
//                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//            }
//        });
//    }
//
//    private void deleteTexture(int i) {
//        if (mTextureIds != null && i < mTextureIds.length && GLES20.glIsTexture(mTextureIds[i])) {
//            GLES20.glDeleteTextures(1, mTextureIds, i);
//            mTextureIds[i] = GLES20.GL_NONE;
//        }
//    }
//
//    private void updateTexture(int i, ByteBuffer buffer, int w, int h, int internalFormat) {
//        if (GLES20.glIsTexture(mTextureIds[i])) {
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureIds[i]);
//            GLTextureUtil.bindTexture2DParams();
//            GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, w, h, internalFormat, GLES20.GL_UNSIGNED_BYTE, buffer);
//        } else {
//            GLES20.glGenTextures(1, mTextureIds, i);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureIds[i]);
//            GLTextureUtil.bindTexture2DParams();
//            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, internalFormat, w, h, 0, internalFormat, GLES20.GL_UNSIGNED_BYTE, buffer);
//        }
//        if (buffer != null) {
//            buffer.clear();
//        }
//    }
//
//    private void updateTexture(Bitmap bitmap) {
//        if (GLES20.glIsTexture(mTextureIds[texture_index_x])) {
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureIds[texture_index_x]);
//            GLTextureUtil.bindTexture2DParams();
//            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, bitmap);
//        } else {
//            GLES20.glGenTextures(1, mTextureIds, texture_index_x);
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureIds[texture_index_x]);
//            GLTextureUtil.bindTexture2DParams();
//            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
//        }
//    }
//
//    protected final void draw() {
//        GLES20.glUseProgram(getProgram());
//
//        preDrawSteps1DataBuffer();
//        preDrawSteps2BindTexture();
//        preDrawSteps3Matrix();
//        preDrawSteps4Other();
//
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);
//
//        afterDraw();
//    }
//
//    protected void afterDraw() {
//        GLES20.glDisableVertexAttribArray(vPositionHandle);
//        GLES20.glDisableVertexAttribArray(vCoordinateHandle);
//        GLES20.glBindTexture(getTextureType(), 0);
//    }
//
//    protected void preDrawSteps1DataBuffer() {
//        // 绑定顶点坐标缓冲
//        mVertexBuffer.position(0);
//        GLES20.glVertexAttribPointer(vPositionHandle, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
//        GLES20.glEnableVertexAttribArray(vPositionHandle);
//
//        // 绑定纹理坐标缓冲
//        mTextureIndexBuffer.position(0);
//        GLES20.glVertexAttribPointer(vCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureIndexBuffer);
//        GLES20.glEnableVertexAttribArray(vCoordinateHandle);
//    }
//
//    protected void preDrawSteps2BindTexture() {
//        switch (mFormat) {
//            case NV12: {
//                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//                GLES20.glBindTexture(getTextureType(), mTextureIds[texture_index_x]);
//                GLES20.glUniform1i(vTextureXHandle, 0);
//
//                GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
//                GLES20.glBindTexture(getTextureType(), mTextureIds[texture_index_y]);
//                GLES20.glUniform1i(vTextureYHandle, 1);
//                break;
//            }
//
//            case I420: {
//                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//                GLES20.glBindTexture(getTextureType(), mTextureIds[texture_index_x]);
//                GLES20.glUniform1i(vTextureXHandle, 0);
//
//                GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
//                GLES20.glBindTexture(getTextureType(), mTextureIds[texture_index_y]);
//                GLES20.glUniform1i(vTextureYHandle, 1);
//
//                GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
//                GLES20.glBindTexture(getTextureType(), mTextureIds[texture_index_z]);
//                GLES20.glUniform1i(vTextureZHandle, 2);
//                break;
//            }
//
//            default: {
//                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//                GLES20.glBindTexture(getTextureType(), mTextureIds[texture_index_x]);
//                GLES20.glUniform1i(vTextureXHandle, 0);
//            }
//        }
//    }
//
//    protected void preDrawSteps3Matrix() {
//        int width = getFrameBufferW();
//        int height = getFrameBufferH();
//
//        // 视口区域大小(归一化映射范围)
//        GLES20.glViewport(0, 0, width, height);
//        // 矩阵变换
//        GlMatrixTools matrix = getMatrix();
//        matrix.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
//
//        float vs = (float) height / width;
//        matrix.frustum(-1, 1, -vs, vs, 3, 7);
//
//        width = getTempTextureW();
//        height = getTempTextureH();
//
//        matrix.pushMatrix();
//        float degree = Math.abs(mDegree) % 180;
//        float scale_y = height * 1f / width;
//        float scale = (degree == 90) ? Math.min(1f / scale_y, vs / 1f) : Math.min(1f, vs / scale_y);
//        matrix.scale(scale, scale, 1f);
//        matrix.rotate(mDegree, 0, 0, 1);
//        matrix.scale(1f, height * 1f / width, 1f);
//        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
//        matrix.popMatrix();
//    }
//
//    protected void preDrawSteps4Other() {
//        GLES20.glUniform1i(vFormatHandle, mFormat.getValue());
//    }
//
//    /**
//     * 先通过 setData() 生成纹理, 再绘制正确方向
//     *
//     * @return frame buffer object texture id
//     */
//    public int onDrawBuffer() {
//        if (!GLES20.glIsProgram(getProgram())) {
//            return GLES20.GL_NONE;
//        }
//
//        if (mFrameBufferMgr != null) {
//            mFrameBufferMgr.bindNext();
//            mFrameBufferMgr.setColor(mRGBA[0], mRGBA[1], mRGBA[2], mRGBA[3]);
//            mFrameBufferMgr.clearColor(true, true, true, true, true);
//            mFrameBufferMgr.clearDepth(true, true);
//            mFrameBufferMgr.clearStencil(true, true);
//
//            draw();
//            mFrameBufferMgr.unbind();
//            return mFrameBufferMgr.getCurrentTextureId();
//        }
//
//        return GLES20.GL_NONE;
//    }
//
//    @Override
//    public void destroy() {
//        super.destroy();
//
//        if (mTask != null) {
//            mTask.destroy();
//        }
//
//        deleteTextures();
//    }
//
//    private void deleteTextures() {
//        if (mTextureIds != null && mTextureIds.length > 0) {
//            GLES20.glDeleteTextures(3, mTextureIds, 0);
//            int length = mTextureIds.length;
//            for (int i = 0; i < length; i++) {
//                mTextureIds[i] = GLES20.GL_NONE;
//            }
//        }
//    }
//
//    private volatile float mDegree;
//
//    public void setRotate(float degree) {
//        mDegree = degree;
//    }
//
//    public float getRotate() {
//        return mDegree;
//    }
//
//    /**
//     * GL环境下, 卡线程完成纹理构建工作, 纹理方向可能是y轴翻转
//     *
//     * @param data 可以是 bitmap \ res id \ byte[] 等数据
//     */
//    public void setTextureData(Object data, boolean initFBO) {
//        if (mTask != null) {
//            mAutoInitFbo = initFBO;
//            mTask.setImageResource(data);
//            queueRunnable(mTask);
//            runTask(true);
//        }
//    }
//
//    /**
//     * GL环境下, 卡线程完成纹理构建工作, 纹理方向可能是y轴翻转
//     *
//     * @param data 可以是 bitmap \ res id \ byte[] 等数据
//     */
//    public void setTextureData(Object data) {
//        setTextureData(data, true);
//    }
//
//    private volatile boolean mAutoInitFbo;
//
//    public enum Format {
//        IDLE(-1),
//        RGBA(0),
//        NV12(1),
//        I420(2),
//        ;
//
//        private int mValue;
//
//        Format(int value) {
//            this.mValue = value;
//        }
//
//        public int getValue() {
//            return mValue;
//        }
//    }
//}
