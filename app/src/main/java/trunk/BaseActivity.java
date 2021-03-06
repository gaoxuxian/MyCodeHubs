package trunk;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/19.
 */
public abstract class BaseActivity extends Activity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        try
        {
            onCreateBaseData();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        onCreateUI(this);
        onCreateFinish();
    }

    public abstract void onCreateBaseData() throws Exception;

    public void onCreateUI(Context context)
    {
        FrameLayout mParent = new FrameLayout(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mParent.setLayoutParams(params);
        setContentView(mParent);
        onCreateChildren(context, mParent, params);
    }

    public abstract void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params);

    public abstract void onCreateFinish();
}