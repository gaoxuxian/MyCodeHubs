package com.xx.avlibrary;

public interface IAudioDecodePloy {
    void setPath(Object path);

    AudioDecodeInfo performDecodeOperation();

    void requestSeekTo(int timeMs);
}
