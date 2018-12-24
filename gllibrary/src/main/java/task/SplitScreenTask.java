package task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import util.AbsTask;
import util.FileUtil;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/21.
 */
public class SplitScreenTask extends AbsTask
{
    public interface Listener
    {
        void onBitmapSucceed(Bitmap bitmap);
    }

    private Listener mListener;
    private Object mBmpObj;
    private Bitmap mBitmap;

    public SplitScreenTask(Context context, Listener listener)
    {
        super(context);
        mListener = listener;
    }

    public void setBitmapRes(Object res)
    {
        mBmpObj = res;
    }

    @Override
    public void runOnThread()
    {
        processBitmap(mBmpObj);
    }

    @Override
    public void executeTaskCallback()
    {
        if (mListener != null)
        {
            mListener.onBitmapSucceed(mBitmap);
            mBitmap = null;
        }
    }

    @Override
    public void destroy()
    {
        super.destroy();
        mListener = null;
    }

    private void processBitmap(Object res)
    {
        if (res != null)
        {
            if (res instanceof Integer)
            {
                mBitmap = BitmapFactory.decodeResource(getContext().getResources(), (int) res);
            }
            else if (res instanceof String && FileUtil.isFileExists((String) res))
            {
                mBitmap = BitmapFactory.decodeFile((String) res);
            }
        }
    }
}
