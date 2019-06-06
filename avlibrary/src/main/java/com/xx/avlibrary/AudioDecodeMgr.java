package com.xx.avlibrary;

import android.content.Context;

public class AudioDecodeMgr {

    private Context mContext;
    private IAudioDecodePloy mDecodePloy;
    private PluginVideoView.AudioDataListener mDataListener;

    public AudioDecodeMgr(Context context, PluginVideoView.AudioDataListener listener) {
        this.mContext = context;
        this.mDataListener = listener;
    }

    public void setPloy(IAudioDecodePloy ploy) {
        this.mDecodePloy = ploy;
    }


}
