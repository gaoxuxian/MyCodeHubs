package trunk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import util.PxUtil;

public class MainActivity extends BaseActivity {

    private ArrayList<SparseArray<Object>> mData;
    private ActivityItemAdapter adapter;
    private ArrayList<Integer>[] mTest;
    private Object[] mTest1 = new Integer[10];

    public static final String activity_package_path = "trunk.";

    @Override
    public void onCreateBaseData() throws Exception {
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(1);
        arrayList.add(2);
        arrayList.add(3);

        ArrayList<String> arrayList1 = new ArrayList<>();
        arrayList1.add("1");
        arrayList1.add("2");
        arrayList1.add("3");

        mTest = new ArrayList[]{arrayList, arrayList1};
        Integer integer = mTest[0].get(0);
        Integer integer1 = mTest[1].get(0);

        SparseArray<Object> map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "android 应用层");
        Intent intent = new Intent();
        Class cls = Class.forName(activity_package_path + "AndroidActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "Open GL ES");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "OpenGLActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "Kotlin");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "KotlinActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);
    }

    @Override
    public void onCreateUI(Context context) {
        FrameLayout mParent = new FrameLayout(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mParent.setLayoutParams(params);
        setContentView(mParent);
        onCreateChildren(context, mParent, params);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LOW_PROFILE);

    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params) {
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2, RecyclerView.VERTICAL, false));
        params = new FrameLayout.LayoutParams(PxUtil.sU_1080p(720), ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        parent.addView(recyclerView, params);
        adapter = new ActivityItemAdapter(mData, new ActivityItemAdapter.Listener() {
            @Override
            public void onClickItem(Intent intent) {
                startActivity(intent);
            }
        });
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
