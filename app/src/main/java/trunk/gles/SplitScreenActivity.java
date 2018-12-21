package trunk.gles;

import trunk.BaseActivity;
import trunk.gles.view.SplitScreenView;
import trunk.gles.view.SplitScreenViewV2;
import util.PxUtil;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;

public class SplitScreenActivity extends BaseActivity
{

    @Override
    public void onCreateBaseData() throws Exception
    {

    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params)
    {
        SplitScreenViewV2 itemView = new SplitScreenViewV2(context);
        params = new FrameLayout.LayoutParams(PxUtil.sU_1080p(1080), PxUtil.sV_1080p(1080));
        params.gravity = Gravity.CENTER;
        parent.addView(itemView, params);
    }

    @Override
    public void onCreateFinish()
    {

    }
}
