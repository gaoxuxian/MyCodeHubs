package com.xx.avlibrary.player;

public interface DecodeListener<T> {
    void prepared();
    void accessData(T info);
    void paused();
    void completed();
}
