package com.xx.avlibrary.player.port;

import java.util.HashMap;

public interface IPlayer {
    void prepare(HashMap<String, Object> params);

    boolean isPreparing();

    void play();

    boolean isPlaying();

    void pause();

    boolean isPaused();

    void stop();

    boolean isStopped();
}
