package trunk.gles;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.xx.avlibrary.gl.util.ByteBufferUtil;
import com.xx.avlibrary.gl.util.GLUtil;
import com.xx.avlibrary.gl.util.GlMatrixTools;
import com.xx.commonlib.PxUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import trunk.BaseActivity;
import trunk.R;

public class TextureOrientationActivity extends BaseActivity {
    Bitmap bitmap = null;
    private ImageView iv1;
    private ImageView iv2;

    @Override
    public void onCreateBaseData() throws Exception {

    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params) {
        Button btn = new Button(context);
        btn.setAllCaps(false);
        btn.setText("GL纹理 生成 bitmap");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        EglCore eglCore = new EglCore();
//                        eglCore.setConfig(v.getContext(), 8, 8, 8, 8, 0, 0, false);
//                        eglCore.initEglContext(null);
//                        EglSurfaceBase surfaceBase = new EglSurfaceBase(eglCore);
//                        int width = PxUtil.sU_1080p(300);
//                        int height = PxUtil.sU_1080p(300);
//                        surfaceBase.createPbufferSurface(width, height);
//                        surfaceBase.makeCurrent();
//
//                        float[] vertex = new float[]{
//                                0.0f, 1.0f, 0.0f,
//                                -1.0f, -1.0f, 0.0f,
//                                1.0f, -1.0f, 0.0f
//                        };
//
//                        int vPositionSize = vertex.length / 3;
//
//                        FloatBuffer mVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(vertex);
//
//                        float[] color = new float[]{
//                                0.0f, 1.0f, 0.0f, 1.0f,
//                                1.0f, 0.0f, 0.0f, 1.0f,
//                                0.0f, 0.0f, 1.0f, 1.0f
//                        };
//
//                        int aColorSize = color.length / 3;
//
//                        FloatBuffer mVertexColorBuffer = ByteBufferUtil.getNativeFloatBuffer(color);
//
//                        // 生成、加载 着色器
//                        int vertex_shader = GLUtil.createShader(GLES20.GL_VERTEX_SHADER, GLUtil.readShaderFromRaw(v.getContext(), R.raw.vertex_shader));
//                        int fragment_shader = GLUtil.createShader(GLES20.GL_FRAGMENT_SHADER, GLUtil.readShaderFromRaw(v.getContext(), R.raw.fragment_shader));
//
//                        // 生成 program
//                        int program = GLUtil.createAndLinkProgram(vertex_shader, fragment_shader);
//
//                        // 获取句柄
//                        int vPosition = GLES20.glGetAttribLocation(program, "vPosition");
//                        int aColor = GLES20.glGetAttribLocation(program, "aColor");
//                        int vMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
//
//                        float sWidthHeight = (float) width / height;
//                        GlMatrixTools tools = new GlMatrixTools();
//                        tools.frustum(-1f, 1f, -1f / sWidthHeight, 1f / sWidthHeight, 3, 5);
//                        tools.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);
//
//                        GLES20.glViewport(0, 0, width, height);
//                        GLES20.glClearColor(1, 0, 0, 1);
//                        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//                        GLES20.glUseProgram(program);
//
//                        GLES20.glUniformMatrix4fv(vMatrix, 1, false, tools.getFinalMatrix(), 0);
//
//                        GLES20.glEnableVertexAttribArray(vPosition);
//                        GLES20.glVertexAttribPointer(vPosition, vPositionSize, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
//
//                        GLES20.glEnableVertexAttribArray(aColor);
//                        GLES20.glVertexAttribPointer(aColor, aColorSize, GLES20.GL_FLOAT, false, 0, mVertexColorBuffer);
//
//                        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vPositionSize);
//
//                        ByteBuffer rgbaBuf = ByteBuffer.allocateDirect(width * height * 4);
//                        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, rgbaBuf);
//                        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//                        bitmap.copyPixelsFromBuffer(rgbaBuf);
//
//                        surfaceBase.releaseEglSurface();
//                        eglCore.release();
//
//                        ThreadUtil.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                iv1.setImageBitmap(bitmap);
//                            }
//                        });
//                    }
//                }).start();
                iv1.setImageBitmap(bitmap);
            }
        });
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.addView(btn, params);

        iv1 = new ImageView(context);
        params = new FrameLayout.LayoutParams(PxUtil.sU_1080p(300), PxUtil.sU_1080p(300));
        params.topMargin = PxUtil.sV_1080p(200);
        parent.addView(iv1, params);

        btn = new Button(context);
        btn.setAllCaps(false);
        btn.setText("GL实时绘制");
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = PxUtil.sV_1080p(500);
        parent.addView(btn, params);

        GLSurfaceView surfaceView = new GLSurfaceView(context);
        surfaceView.setEGLContextClientVersion(GLUtil.getGlSupportVersionInt(context));
        surfaceView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {

            }

            @Override
            public void onDrawFrame(GL10 gl) {
                int width = PxUtil.sU_1080p(300);
                int height = PxUtil.sU_1080p(300);

                float[] vertex = new float[]{
                        0.0f, 1.0f, 0.0f,
                        -1.0f, -1.0f, 0.0f,
                        1.0f, -1.0f, 0.0f
                };

                int vPositionSize = vertex.length / 3;

                FloatBuffer mVertexBuffer = ByteBufferUtil.getNativeFloatBuffer(vertex);

                float[] color = new float[]{
                        0.0f, 1.0f, 0.0f, 1.0f,
                        1.0f, 0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f
                };

                int aColorSize = color.length / 3;

                FloatBuffer mVertexColorBuffer = ByteBufferUtil.getNativeFloatBuffer(color);

                // 生成、加载 着色器
                int vertex_shader = GLUtil.createShader(GLES20.GL_VERTEX_SHADER, GLUtil.readShaderFromRaw(TextureOrientationActivity.this, R.raw.vertex_shader));
                int fragment_shader = GLUtil.createShader(GLES20.GL_FRAGMENT_SHADER, GLUtil.readShaderFromRaw(TextureOrientationActivity.this, R.raw.fragment_shader));

                // 生成 program
                int program = GLUtil.createAndLinkProgram(vertex_shader, fragment_shader);

                // 获取句柄
                int vPosition = GLES20.glGetAttribLocation(program, "vPosition");
                int aColor = GLES20.glGetAttribLocation(program, "aColor");
                int vMatrix = GLES20.glGetUniformLocation(program, "vMatrix");

                float sWidthHeight = (float) width / height;
                GlMatrixTools tools = new GlMatrixTools();
                tools.frustum(-1f, 1f, -1f / sWidthHeight, 1f / sWidthHeight, 3, 5);
                tools.setCamera(0, 0, 3, 0, 0, 0, 0, 1, 0);

                GLES20.glViewport(0, 0, width, height);
                GLES20.glClearColor(1, 0, 0, 1);
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
                GLES20.glUseProgram(program);

                GLES20.glUniformMatrix4fv(vMatrix, 1, false, tools.getFinalMatrix(), 0);

                GLES20.glEnableVertexAttribArray(vPosition);
                GLES20.glVertexAttribPointer(vPosition, vPositionSize, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

                GLES20.glEnableVertexAttribArray(aColor);
                GLES20.glVertexAttribPointer(aColor, aColorSize, GLES20.GL_FLOAT, false, 0, mVertexColorBuffer);

                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vPositionSize);

                ByteBuffer rgbaBuf = ByteBuffer.allocateDirect(width * height * 4);
                GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, rgbaBuf);
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(rgbaBuf);
            }
        });
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        surfaceView.requestRender();
        params = new FrameLayout.LayoutParams(PxUtil.sU_1080p(300), PxUtil.sU_1080p(300));
        params.topMargin = PxUtil.sV_1080p(700);
        parent.addView(surfaceView, params);
    }

    @Override
    public void onCreateFinish() {

    }
}
