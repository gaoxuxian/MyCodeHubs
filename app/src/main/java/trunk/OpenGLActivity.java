package trunk;

import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import util.ByteBufferUtil;

import java.util.ArrayList;

public class OpenGLActivity extends BaseActivity {

    public static final String activity_package_path = "trunk.gles.";

    private ArrayList<SparseArray<Object>> mData;
    private ActivityItemAdapter adapter;

    @Override
    public void onCreateBaseData() throws Exception {
        mData = new ArrayList<>();

        SparseArray<Object> map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "斜分屏转场-动画");
        Intent intent = new Intent();
        Class cls = Class.forName(activity_package_path + "SplitScreenActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "四分屏着色器");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "SplitScreenV2Activity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "菱形扩散转场-动画");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "SplitScreenV3Activity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "图片模糊-动画");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "FuzzyActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "转场-变焦-动画");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "ZoomTransitionActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);
    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params) {
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3, RecyclerView.VERTICAL, false));
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(recyclerView, params);
        adapter = new ActivityItemAdapter(mData, intent -> startActivity(intent));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateFinish() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.destroy();
    }
}
