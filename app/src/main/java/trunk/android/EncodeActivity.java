package trunk.android;

import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import lib.gl.egl.EglCore;
import lib.gl.egl.EglSurfaceBase;
import lib.gl.encode.Encoder;
import lib.gl.filter.common.BmpToTextureFilter;
import lib.gl.filter.common.DisplayImageFilter;
import trunk.BaseActivity;

import android.content.Context;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;
import android.widget.FrameLayout;
import trunk.R;

import java.io.File;

public class EncodeActivity extends BaseActivity {

    @Override
    public void onCreateBaseData() throws Exception {

    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params) {
        Button btn = new Button(context);
        btn.setText("开始编码");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "开始编码", Toast.LENGTH_SHORT).show();
                startToEncode();
            }
        });
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        parent.addView(btn, params);
    }

    @Override
    public void onCreateFinish() {

        MediaCodecList list = new MediaCodecList(MediaCodecList.REGULAR_CODECS);

        MediaFormat videoFormat = MediaFormat.createVideoFormat("video/avc", 1080, 1920);

        String encoderForFormat = list.findEncoderForFormat(videoFormat);

//        MediaCodec encoder = null;
//        MediaCodec encoderByType = null;
//        try
//        {
//            encoder = MediaCodec.createByCodecName(encoderForFormat);
//            encoderByType = MediaCodec.createEncoderByType("video/avc");
//
//            encoder.createInputSurface();
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//
//        MediaCodecInfo[] codecInfos = list.getCodecInfos();
//
//        for (MediaCodecInfo info : codecInfos)
//        {
//            if (!info.isEncoder())
//            {
//                continue;
//            }
//
//            Log.d("xxx", "onCreateFinish: Encoder MediaCodecInfo name is : " + info.getName() + "\n");
//
//            String[] supportedTypes = info.getSupportedTypes();
//
//            for (String supportType : supportedTypes)
//            {
//                Log.d("xxx", "onCreateFinish: Encoder MediaCodecInfo support : " + supportType + "\n");
//            }
//        }
    }

    public void startToEncode(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Context context = EncodeActivity.this;
                int width = 720;
                int height = 720;
                int bitrate = (int) (width * height * 4 * 8 * 0.001f * 30 * 0.25f);
                Encoder encoder = new Encoder(width, height, bitrate, new File(getFilesDir(), "test_encode.mp4"));
                EglCore egl = new EglCore();
                egl.setConfig(context, 8, 8, 8, 8, 0, 0, true);
                egl.initEglContext(null);

                Surface inputSurface = encoder.getInputSurface();
                EglSurfaceBase surfaceBase = new EglSurfaceBase(egl);
                surfaceBase.createWindowSurface(inputSurface);
                surfaceBase.makeCurrent();

                int[] bmp_res = new int[]{
                        R.drawable.open_test,
                        R.drawable.open_test_2,
                        R.drawable.open_test_3,
                        R.drawable.open_test_4,
                        R.drawable.open_test_5
                };

                BmpToTextureFilter bmpToTextureFilter = new BmpToTextureFilter(context);
                bmpToTextureFilter.onSurfaceCreated(null);
                bmpToTextureFilter.onSurfaceChanged(width, height);
                bmpToTextureFilter.initFrameBuffer(width, height);

                DisplayImageFilter displayImageFilter = new DisplayImageFilter(context);
                displayImageFilter.onSurfaceCreated(null);
                displayImageFilter.onSurfaceChanged(width, height);
                displayImageFilter.setTextureWH(width, height);

                for (int i = 0; i < bmp_res.length; i++) {
                    bmpToTextureFilter.setBitmapRes(bmp_res[i]);
                    int textureID = bmpToTextureFilter.onDrawBuffer(0);

                    for (int k = 0; k < 30; k++) {
                        encoder.drainEncoder(false);
                        displayImageFilter.onDrawFrame(textureID);
                        surfaceBase.setPresentationTime((i * 30 + k % 30) * 1000000000L / 30);
                        surfaceBase.swapBuffers();
                    }
                }

                encoder.drainEncoder(true);
                encoder.release();

                inputSurface.release();
                surfaceBase.releaseEglSurface();

                bmpToTextureFilter.destroy();
                displayImageFilter.destroy();

                egl.release();

                Log.d("xxx", "run: 编码结束");
            }
        }).start();
    }
}
