package com.xx.avlibrary.player.impl;

import com.xx.avlibrary.player.entry.VideoDecodeInfo;
import com.xx.avlibrary.player.port.decode.IVideoDecoder;

public class PreviewVideoDecoder implements IVideoDecoder {

    @Override
    public void prepare(Object params) {

    }

    @Override
    public VideoDecodeInfo performDecodeOperation() {
        return null;
    }

    @Override
    public void requestSeekTo(int timeMs) {

    }
}
