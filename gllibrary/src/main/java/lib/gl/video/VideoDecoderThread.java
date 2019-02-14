package lib.gl.video;

import android.util.Log;
import android.view.Surface;

public class VideoDecoderThread extends Thread {
    private volatile boolean mRelease;
    private VideoDecoder mDecoder;

    public VideoDecoderThread(String path, Surface surface) {
        mDecoder = new VideoDecoder(path, surface);
        setPriority(Thread.MAX_PRIORITY);
    }

    @Override
    public void run() {
        while (!mRelease) {
            mDecoder.run();
        }

        if (mRelease) {
            Log.d("xxx", "run: mDecoder.release();");
            mDecoder.destroy();
        }
    }

    public VideoDecoder getDecoder() {
        return mDecoder;
    }

    public void release() {
        mRelease = true;
        mDecoder.release();
    }
}
