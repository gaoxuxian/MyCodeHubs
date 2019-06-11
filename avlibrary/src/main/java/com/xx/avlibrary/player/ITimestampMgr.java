package com.xx.avlibrary.player;

public interface ITimestampMgr<T> {
    void setTimestampManager(T mgr);
    T getTimestampManager();
}
