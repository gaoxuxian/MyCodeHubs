package com.xx.androiddemo.inke.giftplayer;

import android.content.Context;
import android.opengl.GLES20;

import com.xx.avlibrary.gl.filter.GPUFilterType;
import com.xx.avlibrary.gl.filter.GPUImageFilter;
import com.xx.avlibrary.gl.util.GlMatrixTools;

public class GPUImageGiftFilter extends GPUImageFilter {
    private int vTextureMatrix;
    private float[] textureMatrix;

    private static final String VERTEX_STRING =
            "attribute vec4 vPosition;\n" +
                    "attribute vec2 vCoordinate;\n" +
                    "uniform mat4 vMatrix;\n" +
                    "uniform mat4 vTextureMatrix;\n" +
                    "\n" +
                    "varying vec2 aCoordinate;\n" +
                    "\n" +
                    "void main(){\n" +
                    "    gl_Position = vMatrix * vPosition;\n" +
                    "    aCoordinate = vec2((vTextureMatrix * vec4(vCoordinate, 1., 1.)).xy);\n" +
                    "}";

    private static final String FRAGMENT_STRING =
            "#extension GL_OES_EGL_image_external:require\n" +
                    "precision lowp float;\n" +
                    "\n" +
                    "uniform samplerExternalOES vTexture;\n" +
                    "varying vec2 aCoordinate;\n" +
                    "\n" +
                    "void main() {\n" +
                    "    vec4 tGray = texture2D(vTexture, vec2(aCoordinate.x/2.0, aCoordinate.y));\n" +
                    "    vec4 tColor = texture2D(vTexture, vec2(aCoordinate.x/2.0 + 0.5, aCoordinate.y));\n" +
                    "    gl_FragColor = vec4(tColor.rgb, tGray.r);\n" +
                    "}";

    public GPUImageGiftFilter(Context context) {
        super(context, VERTEX_STRING, FRAGMENT_STRING);
    }

    @Override
    protected void onInitProgramHandle() {
        super.onInitProgramHandle();
        vTextureMatrix = GLES20.glGetUniformLocation(getProgram(), "vTextureMatrix");
    }

    public void setTextureMatrix(float[] matrix) {
        textureMatrix = matrix;
    }

    @Override
    protected void preDrawSteps3Matrix(boolean drawBuffer) {
        int width = drawBuffer ? getFrameBufferW() : getSurfaceW();
        int height = drawBuffer ? getFrameBufferH() : getSurfaceH();

        // 视口区域大小(归一化映射范围)
        GLES20.glViewport(0, 0, width, height);
        // 矩阵变换
        GlMatrixTools matrix = getMatrix();
        matrix.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);

        float vs = (float) height / width;
        matrix.frustum(-1, 1, -vs, vs, 3, 7);

        width = getTextureW();
        height = getTextureH();

        matrix.pushMatrix();
        float scale_y = height * 1f / width;
        float scale = Math.max(1f, vs / scale_y);
        matrix.scale(scale, scale, 1f);
        matrix.scale(1f, height * 1f / width, 1f);
        GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, matrix.getFinalMatrix(), 0);
        matrix.popMatrix();
    }

    @Override
    protected void preDrawSteps4Other(boolean drawBuffer) {
        GLES20.glUniformMatrix4fv(vTextureMatrix, 1, false, textureMatrix, 0);
    }

    @Override
    public GPUFilterType getFilterType() {
        return null;
    }
}
