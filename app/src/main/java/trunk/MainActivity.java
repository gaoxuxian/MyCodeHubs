package trunk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xx.commonlib.PxUtil;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends BaseActivity {

    private ArrayList<SparseArray<Object>> mData;
    private ActivityItemAdapter adapter;

    public static final String activity_package_path = "trunk.";

    @Override
    public void onCreateBaseData() throws Exception {
        mData = new ArrayList<>();

        SparseArray<Object> map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "java");
        Intent intent = new Intent();
        Class cls = Class.forName(activity_package_path + "JavaActivity");
        intent.setClass(this, cls);
        map.put(ActivityItemAdapter.DataKey.CLASS_INTENT, intent);
        mData.add(map);

        map = new SparseArray<>();
        map.put(ActivityItemAdapter.DataKey.ITEM_TITLE, "android");
        intent = new Intent();
        cls = Class.forName(activity_package_path + "AndroidActivity");
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
        super.onCreateUI(context);

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
//                intent = new Intent();
//                intent.setAction(Intent.ACTION_PICK);
//                intent.setType("video/*");
//                startActivityForResult(intent, 2);
//                Glide.with(MainActivity.this).asBitmap().load(R.drawable.open_test_5).transform(new BitmapTransformation() {
//                    @Override
//                    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
//                        return toTransform;
//                    }
//
//                    @Override
//                    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
//
//                    }
//                }).into(new CustomTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        if (resource != null) {
//                            resource.prepareToDraw();
//                        }
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                    }
//                });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            Cursor cursor = null;
            try {
                if (data != null && data.getData() != null) {
                    cursor = getContentResolver().query(data.getData(), new String[]{MediaStore.Video.Media.DATA}, null, null, null);

                    if (cursor != null && cursor.moveToFirst()) {
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        Log.d("xxxx", "onActivityResult , 选中的视频绝对路径 == " + path);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
    }
}
