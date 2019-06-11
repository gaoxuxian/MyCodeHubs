package com.xx.avlibrary.player.port;

public interface IPlayerListenerMgr<P> {
    void registerPlayerListener(P listener);
    P getPlayerListener();
    void unregisterPlayerListener();
}
