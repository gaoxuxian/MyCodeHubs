package trunk.gles;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;

import trunk.BaseActivity;
import trunk.gles.view.RandomSquareTransitionView;
import trunk.gles.view.SingleDragTransitionView;
import util.PxUtil;

public class RandomSquareTransitionActivity extends BaseActivity
{
    private RandomSquareTransitionView mItemView;

    @Override
    public void onCreateBaseData() throws Exception
    {

    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params)
    {
        mItemView = new RandomSquareTransitionView(context);
        params = new FrameLayout.LayoutParams(PxUtil.sU_1080p(1080), PxUtil.sU_1080p(1920));
        params.gravity = Gravity.CENTER;
        parent.addView(mItemView, params);
    }

    @Override
    public void onCreateFinish()
    {

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

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mItemView != null)
        {
            mItemView.onResume();
        }
    }
}
