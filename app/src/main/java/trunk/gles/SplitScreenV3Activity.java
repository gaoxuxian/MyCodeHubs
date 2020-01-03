package trunk.gles;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.xx.commonlib.PxUtil;

import trunk.BaseActivity;
import trunk.gles.view.SplitScreenViewV3;

public class SplitScreenV3Activity extends BaseActivity
{
    private SplitScreenViewV3 mItemView;

    @Override
    public void onCreateBaseData() throws Exception
    {

    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mItemView = new SplitScreenViewV3(context);
        params = new FrameLayout.LayoutParams(PxUtil.sU_1080p(1080), PxUtil.sU_1080p(2160));
        params.gravity = Gravity.CENTER;
        parent.addView(mItemView, params);
    }

    @Override
    public void onCreateFinish()
    {

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mItemView != null)
        {
            mItemView.onResume();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mItemView != null)
        {
            mItemView.onPause();
        }
    }
}
