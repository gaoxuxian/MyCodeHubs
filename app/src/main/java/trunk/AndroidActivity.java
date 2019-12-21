package trunk;

import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class AndroidActivity extends BaseActivity {

    public static final String activity_package_path = "trunk.android.";

    private ArrayList<SparseArray<Object>> mData;
    private ActivityItemAdapter adapter;

    @Override
    public void onCreateBaseData() throws Exception {
        mData = new ArrayList<>();

        SparseArray<Object> map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "自制媒体播放器");
        Intent intent = new Intent();
        Class cls = Class.forName(activity_package_path + "MyMediaPlayerActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "Encoder 试用");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "EncodeActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "音频 播放");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "AudioActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "音频 录制");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "SoundRecordActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "测试 LiveData");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "LiveDataActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "测试 ConstraintLayout");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "LayoutTestActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "测试 MotionEvent");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "EventTestActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "测试 rewind 视频");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "RewindTestActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "测试 Window");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "WindowTestActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "测试 节奏");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "RhythmTestActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "测试 事件分发");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "TouchEventActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "测试 线程");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "MultThreadActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "测试 RecyclerView");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "MainActivity45");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "测试 NestedScrolling");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "NestedScrollActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "测试服务");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "ServiceTestActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "测试 GC");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "GCTestActivity");
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
        adapter = new ActivityItemAdapter(mData, this::startActivity);
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
