package com.xx.avlibrary.player.impl;

public interface IDecodeTask extends Runnable {
    void prepare(Object path);

    void play();

    void pause();

    void end();
}
