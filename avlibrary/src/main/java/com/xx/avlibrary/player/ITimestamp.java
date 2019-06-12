package com.xx.avlibrary.player;

public interface ITimestamp {
    void updateAudioTimestamp(long timeMs);

    /**
     * 视频时间同步到音频时间
     * @param vTimeMs 视频时间
     * @return (视频时间戳 - 音频时间戳)
     */
    long syncAVTimestamp(long vTimeMs);
}
