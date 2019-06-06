package com.xx.avlibrary;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.view.Surface;
import android.view.TextureView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.xx.avlibrary.gl.egl.EglCore;
import com.xx.avlibrary.gl.egl.EglSurfaceBase;

import java.lang.ref.WeakReference;

public class PluginVideoView extends ConstraintLayout {
    private AudioTrack mAudioPlayer;
    private TextureView mVideoPlayer;

    private AudioDecodeMgr mAudioDecodeMgr;

    public PluginVideoView(Context context) {
        super(context);
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
}
