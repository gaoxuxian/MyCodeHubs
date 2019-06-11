package com.xx.avlibrary.player.impl;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public abstract class DecodeHandler extends Handler {
    public final static int prepare = 666;
    public final static int play    = 777;
    public final static int pause   = 888;
    public final static int end     = 999;

    public DecodeHandler(Looper looper) {
        super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case prepare: {
                handlePrepare(msg);
                break;
            }

            case play: {
                handlePlay();
                break;
            }

            case pause: {
                handlePause();
                break;
            }

            case end: {
                handleEnd();
                break;
            }
        }
    }

    protected abstract void handlePrepare(Message msg);
    protected abstract void handlePlay();
    protected abstract void handlePause();
    protected abstract void handleEnd();
}
