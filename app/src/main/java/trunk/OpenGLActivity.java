package trunk;

import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "图片模糊-静态");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "FuzzyActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "重影-动画");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "GhostingActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "转场- 变焦 -动画");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "ZoomTransitionActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "转场- 矩形渐变 -动画");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "RectSmoothTransitionActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "转场- 平移 -动画");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "TranslationTransitionActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "转场- 扩散圆 -动画");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "SpreadRoundTransitionActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "转场- 抖动_rgb分离 -动画");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "ShakeTransitionActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "转场- perlin -动画");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "PerlinTransitionActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "转场- 翻页 -动画");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "PagingTransitionActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "转场- 模糊叠化 -动画");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "FuzzyTransitionActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "转场- 旋转叠化 -动画");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "RotateTransitionActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "转场- Circle Crop -动画");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "CircleCropTransitionActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "转场- 噪声亮度融化(上->下) -动画");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "LuminanceMeltUpDownTransitionActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "转场- 噪声亮度融化(下->上) -动画");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "LuminanceMeltDownUpTransitionActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "转场- 色彩渐变 -动画");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "ColorDistanceTransitionActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "转场- 色彩渐变 + 重影 -动画");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "ColorGhostingTransitionActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "转场- 方块对称翻转 -动画");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "SquareAnimTransitionActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "转场- 色彩平移 -动画");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "ColorTranslationTransitionActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "转场- 色彩平移-v2 -动画");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "ColorTranslationTransitionActivityV2");
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
