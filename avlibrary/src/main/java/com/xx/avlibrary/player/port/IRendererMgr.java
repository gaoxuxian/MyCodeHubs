package com.xx.avlibrary.player.port;

public interface IRendererMgr<R> {
    void setRenderer(R renderer);
    R getRenderer();
}
