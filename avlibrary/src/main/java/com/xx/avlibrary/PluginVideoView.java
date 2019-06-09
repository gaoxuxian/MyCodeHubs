package com.xx.avlibrary;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioTrack;
import android.view.TextureView;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

public class PluginVideoView extends ConstraintLayout implements TextureView.SurfaceTextureListener {
    private AudioTrack mAudioPlayer;
    private TextureView mVideoPlayer;

    private AudioDecodeMgr mAudioDecodeMgr;

    private RenderThread mRendererThread;

    public PluginVideoView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        initThread();
        initView(context);
    }

    private void initThread() {
        mRendererThread = new RenderThread(this);
        mRendererThread.setPreserveEGLOnPause(true);
        mRendererThread.start();
    }

    private void initView(Context context) {
        mVideoPlayer = new TextureView(context);
        mVideoPlayer.setId(View.generateViewId());
        mVideoPlayer.setSurfaceTextureListener(this);
        ConstraintLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_CONSTRAINT, LayoutParams.MATCH_CONSTRAINT);
        this.addView(mVideoPlayer, params);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mRendererThread != null) {
            mRendererThread.onSurfaceCreated(surface);
            mRendererThread.onSurfaceChanged(width, height);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (mRendererThread != null) {
            mRendererThread.onSurfaceChanged(width, height);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public interface Renderer {
        void onSurfaceCreated();

        void onSurfaceChanged(int width, int height);

        void onDrawFrame(@NonNull DrawInfo info);

        void onSurfaceDestroyed();
    }

    public static class DrawInfo {
        public int fgFrameTextureId;
        public int previousFrameTextureId;
        public int presentFrameTextureId;
    }

    private volatile IController mPlayerController;

    /**
     *
     */
    public void setPlayerController(IController controller) {
        mPlayerController = controller;
    }

    public IController getPlayerController() {
        return mPlayerController;
    }

    private static class VideoDataThread extends Thread {
        @Override
        public void run() {
            super.run();
        }
    }

    private static class AudioDataThread extends Thread {
        @Override
        public void run() {
            super.run();
        }
    }
}
