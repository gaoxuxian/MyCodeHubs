package lib.gl.decode;

public interface AbsDecoder extends Runnable{
    void setPause(boolean pause);

    void setRepeat(boolean repeat);

    int getVideoWidth();

    int getVideoHeight();

    int getVideoRotation();

    int getVideoDuration();

    void release();

    void destroy();
}
