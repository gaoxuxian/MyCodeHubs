package com.xx.avlibrary.player.impl;

import android.os.Looper;
import android.os.Message;

public abstract class DecodeTask<T extends DecodeHandler> implements IDecodeTask {
    private volatile T mDecodeHandler;

    @Override
    public void prepare(Object path) {
        if (mDecodeHandler != null) {
            Message.obtain(mDecodeHandler, DecodeHandler.prepare, path).sendToTarget();
        }
    }

    @Override
    public void play() {
        if (mDecodeHandler != null) {
            Message.obtain(mDecodeHandler, DecodeHandler.play).sendToTarget();
        }
    }

    @Override
    public void pause() {
        if (mDecodeHandler != null) {
            Message.obtain(mDecodeHandler, DecodeHandler.pause).sendToTarget();
        }
    }

    @Override
    public void end() {
        if (mDecodeHandler != null) {
            Message.obtain(mDecodeHandler, DecodeHandler.end).sendToTarget();
        }
    }

    @Override
    public void run() {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        mDecodeHandler = initHandler();
        Looper.loop();
    }

    protected abstract T initHandler();
}
