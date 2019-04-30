package lib.gl.filter.rhythm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import lib.gl.util.AbsTask;
import util.FileUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/23.
 */
public class TextureResTask extends AbsTask {
    private volatile Listener mListener;
    private volatile Object mImageRes;
    private volatile Object mImage;
    private volatile int mImageWidth;
    private volatile int mImageHeight;

    public interface Listener {
        void onStart(int width, int height);

        void onImageSucceed(Bitmap bitmap);

        void onImageSucceed(ByteBuffer bitmap);
    }

    public TextureResTask(Context context, Listener listener) {
        super(context);
        this.mListener = listener;
    }

    public void setImageResource(Object res) {
        this.mImageRes = res;
    }

    @Override
    public void runOnThread() {
        try {
            processBitmap();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void executeTaskCallback() {
        if (mListener != null) {
            mListener.onStart(mImageWidth, mImageHeight);

            if (mImage != null) {
                if (mImage instanceof Bitmap) {
                    mListener.onImageSucceed((Bitmap) mImage);
                } else if (mImage instanceof ByteBuffer) {
                    mListener.onImageSucceed((ByteBuffer) mImage);
                }
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();

        if (mImage != null) {
            mImage = null;
        }

        mListener = null;
    }

    public void processBitmap() {
        if (mImageRes != null) {
            if (mImageRes instanceof String) {
                if (((String) mImageRes).startsWith("file:///android_asset/")) {
                    InputStream open = null;
                    try {
                        open = getContext().getAssets().open((String) mImageRes);
                        mImage = BitmapFactory.decodeStream(open);
                        mImageWidth = ((Bitmap) mImage).getWidth();
                        mImageHeight = ((Bitmap) mImage).getHeight();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (open != null) {
                            try {
                                open.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else if (FileUtil.isFileExists((String) mImageRes)) {
                    mImage = BitmapFactory.decodeFile((String) mImageRes);
                    mImageWidth = ((Bitmap) mImage).getWidth();
                    mImageHeight = ((Bitmap) mImage).getHeight();
                }
            } else if (mImageRes instanceof Bitmap) {
                mImage = mImageRes;
                mImageWidth = ((Bitmap) mImageRes).getWidth();
                mImageHeight = ((Bitmap) mImageRes).getHeight();
            } else if (mImageRes instanceof Integer) {
                mImage = BitmapFactory.decodeResource(getContext().getResources(), (Integer) mImageRes);
                mImageWidth = ((Bitmap) mImage).getWidth();
                mImageHeight = ((Bitmap) mImage).getHeight();
            }
        }
    }
}
