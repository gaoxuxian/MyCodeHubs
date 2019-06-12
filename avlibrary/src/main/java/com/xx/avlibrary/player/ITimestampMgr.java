package com.xx.avlibrary.player;

public interface ITimestampMgr<T> {
    void setTimestampManager(T timestamp);
    T getTimestampManager();
}
