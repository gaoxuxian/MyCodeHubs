package com.xx.avlibrary;

public interface IController extends IRendererMgr<PluginVideoView.Renderer> {
    void prepare();

    void play();

    void pause();

    void stop();
}
