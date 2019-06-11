package com.xx.avlibrary.player.port;

public interface IDecodeMgr<A, V> {
    void setAudioDecoder(A decoder);
    A getAudioDecoder();
    void setVideoDecoder(V decoder);
    V getVideoDecoder();
}
