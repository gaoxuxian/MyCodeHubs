package com.xx.avlibrary.player.impl;

import com.xx.avlibrary.player.entry.AudioDecodeInfo;
import com.xx.avlibrary.player.port.decode.IAudioDecoder;

public class PreviewAudioDecoder implements IAudioDecoder {

    @Override
    public void prepare(Object params) {

    }

    @Override
    public AudioDecodeInfo performDecodeOperation() {
        return null;
    }

    @Override
    public void requestSeekTo(int timeMs) {

    }
}
