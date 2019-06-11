package com.xx.avlibrary.player.impl;

import com.xx.avlibrary.player.PluginVideoView;
import com.xx.avlibrary.player.port.decode.IAudioDecoder;
import com.xx.avlibrary.player.port.IControl;
import com.xx.avlibrary.player.port.decode.IVideoDecoder;

public class PreviewControl implements IControl {
    private volatile IAudioDecoder mAudioDecoder;
    private volatile IVideoDecoder mVideoDecoder;
    private volatile PluginVideoView.Renderer mRenderer;
    private volatile PluginVideoView.PlayerListener mPlayerListener;

    @Override
    public void setAudioDecoder(IAudioDecoder decoder) {
        mAudioDecoder = decoder;
    }

    @Override
    public IAudioDecoder getAudioDecoder() {
        return mAudioDecoder;
    }

    @Override
    public void setVideoDecoder(IVideoDecoder decoder) {
        mVideoDecoder = decoder;
    }

    @Override
    public IVideoDecoder getVideoDecoder() {
        return mVideoDecoder;
    }

    @Override
    public void setRenderer(PluginVideoView.Renderer renderer) {
        mRenderer = renderer;
    }

    @Override
    public PluginVideoView.Renderer getRenderer() {
        return mRenderer;
    }

    @Override
    public void registerPlayerListener(PluginVideoView.PlayerListener listener) {
        mPlayerListener = listener;
    }

    @Override
    public PluginVideoView.PlayerListener getPlayerListener() {
        return mPlayerListener;
    }

    @Override
    public void unregisterPlayerListener() {
        mPlayerListener = null;
    }
}
