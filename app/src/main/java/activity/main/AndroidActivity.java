package activity.main;

import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class AndroidActivity extends BaseActivity {

    public static final String activity_android_package_path = "activity.android.";

    private ArrayList<SparseArray<Object>> mData;
    private ActivityItemAdapter adapter;

    @Override
    public void onCreateBaseData() throws Exception {
        mData = new ArrayList<>();
    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params) {
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3, RecyclerView.VERTICAL, false));
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
