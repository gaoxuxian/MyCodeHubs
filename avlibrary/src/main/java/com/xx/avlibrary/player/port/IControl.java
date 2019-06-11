package com.xx.avlibrary.player.port;

import com.xx.avlibrary.player.PluginVideoView;
import com.xx.avlibrary.player.port.decode.IAudioDecoder;
import com.xx.avlibrary.player.port.decode.IVideoDecoder;

public interface IControl extends IRendererMgr<PluginVideoView.Renderer>, IDecodeMgr<IAudioDecoder, IVideoDecoder>,
        IPlayerListenerMgr<PluginVideoView.PlayerListener> {

}
