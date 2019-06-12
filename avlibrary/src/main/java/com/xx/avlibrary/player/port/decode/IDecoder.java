package com.xx.avlibrary.player.port.decode;

public interface IDecoder<T> {
    void prepare(Object params);
    T performDecodeOperation();
    void requestSeekTo(int timeMs);
}
