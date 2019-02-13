package lib.gl.video;

/**
 * @author Gxx
 * Created by Gxx on 2019/2/11.
 */
public interface VideoDecodeListener
{
    void onPrepareSucceed(VideoDecoder decoder);

    void onFirstFrameAutoRender(VideoDecoder decoder);

    void onEnd(VideoDecoder decoder);
}
