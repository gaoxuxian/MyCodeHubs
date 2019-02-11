package trunk.android;

import trunk.BaseActivity;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaMetadata;
import android.util.Log;
import android.widget.FrameLayout;

import java.io.IOException;

public class EncodeActivity extends BaseActivity
{

    @Override
    public void onCreateBaseData() throws Exception
    {

    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params)
    {

    }

    @Override
    public void onCreateFinish()
    {

        MediaCodecList list = new MediaCodecList(MediaCodecList.REGULAR_CODECS);

        MediaFormat videoFormat = MediaFormat.createVideoFormat("video/avc", 1080, 1920);

        String encoderForFormat = list.findEncoderForFormat(videoFormat);

        MediaCodec encoder = null;
        MediaCodec encoderByType = null;
        try
        {
            encoder = MediaCodec.createByCodecName(encoderForFormat);
            encoderByType = MediaCodec.createEncoderByType("video/avc");

            encoder.createInputSurface();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        MediaCodecInfo[] codecInfos = list.getCodecInfos();

        for (MediaCodecInfo info : codecInfos)
        {
            if (!info.isEncoder())
            {
                continue;
            }

            Log.d("xxx", "onCreateFinish: Encoder MediaCodecInfo name is : " + info.getName() + "\n");

            String[] supportedTypes = info.getSupportedTypes();

            for (String supportType : supportedTypes)
            {
                Log.d("xxx", "onCreateFinish: Encoder MediaCodecInfo support : " + supportType + "\n");
            }
        }
    }
}
