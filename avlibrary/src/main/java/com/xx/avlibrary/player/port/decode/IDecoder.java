package com.xx.avlibrary.player.port.decode;

public interface IDecoder<T> {
    void setPath(Object path);
    T performDecodeOperation();
    void requestSeekTo(int timeMs);
}
