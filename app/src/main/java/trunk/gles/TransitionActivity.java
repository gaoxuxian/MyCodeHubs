package trunk.gles;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.xx.avlibrary.gl.filter.GPUTransitionFilterType;
import trunk.BaseActivity;
import trunk.R;
import trunk.gles.view.TransitionView;
import util.PxUtil;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class TransitionActivity extends BaseActivity
{
    private TransitionView mItemView;
    private FilterAdapter mAdapter;

    @Override
    public void onCreateBaseData() throws Exception
    {
        ArrayList<FilterInfo> data = new ArrayList<>();

        FilterInfo info = new FilterInfo();
        info.mName = "(0)-无";
        info.mBmpRes = R.drawable._filter_0;
        info.mType = GPUTransitionFilterType.NONE;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(1)-变焦";
        info.mBmpRes = R.drawable._filter_1;
        info.mType = GPUTransitionFilterType.ZOOM;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(2)-矩形渐变";
        info.mBmpRes = R.drawable._filter_2;
        info.mType = GPUTransitionFilterType.SMOOTHNESS;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(3)-平移";
        info.mBmpRes = R.drawable._filter_3;
        info.mType = GPUTransitionFilterType.TRANSLATION;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(4)-扩散圆";
        info.mBmpRes = R.drawable._filter_4;
        info.mType = GPUTransitionFilterType.SPREAD_ROUND;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(5)-抖动+rgb分离";
        info.mBmpRes = R.drawable._filter_5;
        info.mType = GPUTransitionFilterType.SHAKE;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(6)-佩尔林算法";
        info.mBmpRes = R.drawable._filter_6;
        info.mType = GPUTransitionFilterType.PERLIN;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(7)-翻页";
        info.mBmpRes = R.drawable._filter_7;
        info.mType = GPUTransitionFilterType.PAGING;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(8)-模糊";
        info.mBmpRes = R.drawable._filter_8;
        info.mType = GPUTransitionFilterType.FUZZY;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(9)-自旋";
        info.mBmpRes = R.drawable._filter_9;
        info.mType = GPUTransitionFilterType.SPIN;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(10)-Circle crop";
        info.mBmpRes = R.drawable._filter_10;
        info.mType = GPUTransitionFilterType.CIRCLE_CROP;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(11)-亮度融化";
        info.mBmpRes = R.drawable._filter_11;
        info.mType = GPUTransitionFilterType.LUMINANCE_MELT;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(12)-色彩渐变";
        info.mBmpRes = R.drawable._filter_12;
        info.mType = GPUTransitionFilterType.COLOR_DISTANCE;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(13)-方块动画";
        info.mBmpRes = R.drawable._filter_13;
        info.mType = GPUTransitionFilterType.SQUARE_ANIM;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(14)-色彩渐变 + 重影";
        info.mBmpRes = R.drawable._filter_14;
        info.mType = GPUTransitionFilterType.COLOR_GHOSTING;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(15)-色彩扫描";
        info.mBmpRes = R.drawable._filter_15;
        info.mType = GPUTransitionFilterType.COLOR_SCAN;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(16)-色彩扫描v2";
        info.mBmpRes = R.drawable._filter_16;
        info.mType = GPUTransitionFilterType.COLOR_SCAN_V2;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(17)-颗粒感";
        info.mBmpRes = R.drawable._filter_17;
        info.mType = GPUTransitionFilterType.PARTICLES;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(18)-撕裂感";
        info.mBmpRes = R.drawable._filter_18;
        info.mType = GPUTransitionFilterType.TEAR;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(19)-模糊放大";
        info.mBmpRes = R.drawable._filter_19;
        info.mType = GPUTransitionFilterType.FUZZY_ZOOM;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(20)-单方向拉扯";
        info.mBmpRes = R.drawable._filter_20;
        info.mType = GPUTransitionFilterType.SINGLE_DRAG;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(21)-随机方块";
        info.mBmpRes = R.drawable._filter_21;
        info.mType = GPUTransitionFilterType.RANDOM_SQUARE;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(22)-旋转闪白";
        info.mBmpRes = R.drawable._filter_22;
        info.mType = GPUTransitionFilterType.ROTATE_WHITE;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(23)-放大旋转";
        info.mBmpRes = R.drawable._filter_23;
        info.mType = GPUTransitionFilterType.ROTATE_ZOOM;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(24)-纯缩放";
        info.mBmpRes = R.drawable._filter_24;
        info.mType = GPUTransitionFilterType.JUST_EXTEND;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(25)-虚化放大";
        info.mBmpRes = R.drawable._filter_25;
        info.mType = GPUTransitionFilterType.NOISE_BLUR_ZOOM;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(26)-旋转运动模糊";
        info.mBmpRes = R.drawable._filter_25;
        info.mType = GPUTransitionFilterType.MOTION_BLUR;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(27)-右移";
        info.mBmpRes = R.drawable._filter_27;
        info.mType = GPUTransitionFilterType.MOVE_X_RIGHT;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(28)-左移";
        info.mBmpRes = R.drawable._filter_28;
        info.mType = GPUTransitionFilterType.MOVE_X_LEFT;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(29)-上移";
        info.mBmpRes = R.drawable._filter_29;
        info.mType = GPUTransitionFilterType.MOVE_Y_UP;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(30)-下移";
        info.mBmpRes = R.drawable._filter_30;
        info.mType = GPUTransitionFilterType.MOVE_Y_DOWN;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(31)-放大";
        info.mBmpRes = R.drawable._filter_31;
        info.mType = GPUTransitionFilterType.MOTION_ZOOM_OUT_ZOOM_IN;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(32)-缩小";
        info.mBmpRes = R.drawable._filter_32;
        info.mType = GPUTransitionFilterType.MOTION_ZOOM_IN_ZOOM_OUT;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(33)-径向模糊-缩小";
        info.mBmpRes = R.drawable._filter_32;
        info.mType = GPUTransitionFilterType.RADIAL_BLUR_ZOOM_OUT;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(33)-径向模糊-放大";
        info.mBmpRes = R.drawable._filter_32;
        info.mType = GPUTransitionFilterType.RADIAL_BLUR_ZOOM_IN;
        data.add(info);

        info = new FilterInfo();
        info.mName = "(34)-周期旋转";
        info.mBmpRes = R.drawable._filter_32;
        info.mType = GPUTransitionFilterType.ROTATE_ZOOM_V2;
        data.add(info);

        mAdapter = new FilterAdapter(data, type -> mItemView.setTransitionFilter(type));
    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params)
    {
        LinearLayout outsideLayout = new LinearLayout(context);
        outsideLayout.setOrientation(LinearLayout.VERTICAL);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(outsideLayout, params);
        {
            mItemView = new TransitionView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.weight = 1f;
            outsideLayout.addView(mItemView, lp);

            RecyclerView mRecyclerView = new RecyclerView(context);
            mRecyclerView.setBackgroundColor(Color.WHITE);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration()
            {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
                {
                    super.getItemOffsets(outRect, view, parent, state);

                    outRect.left = PxUtil.sU_1080p(8);
                    outRect.right = PxUtil.sU_1080p(8);
                }
            });
            mRecyclerView.setAdapter(mAdapter);
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxUtil.sU_1080p(360));
            outsideLayout.addView(mRecyclerView, lp);
        }

        Button mChangeRatioBtn = new Button(context);
        mChangeRatioBtn.setText("切换图片比例");
        mChangeRatioBtn.setOnClickListener(v -> mItemView.changePreviewBmpRatio());
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = PxUtil.sU_1080p(370);
        parent.addView(mChangeRatioBtn, params);
    }

    @Override
    public void onCreateFinish()
    {

    }

    @Override
    protected void onPause()
    {
        super.onPause();

        mItemView.onPause();
        mAdapter.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        mItemView.onResume();
    }

    private static class FilterAdapter extends RecyclerView.Adapter implements View.OnClickListener
    {
        private ArrayList<FilterInfo> mData;
        private Listener mListener;
        private GPUTransitionFilterType mSelectedType = GPUTransitionFilterType.NONE;

        interface Listener
        {
            void onItemClick(GPUTransitionFilterType type);
        }

        FilterAdapter(ArrayList<FilterInfo> data, Listener listener)
        {
            mData = data;
            mListener = listener;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            FilterItemView itemView = new FilterItemView(parent.getContext());
            itemView.setOnClickListener(this);
            ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(PxUtil.sU_1080p(300), ViewGroup.LayoutParams.MATCH_PARENT);
            itemView.setLayoutParams(params);
            return new RecyclerView.ViewHolder(itemView) {};
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
        {
            FilterItemView itemView = (FilterItemView) holder.itemView;
            FilterInfo info = mData.get(position);
            if (info != null)
            {
                itemView.setFilterName(info.mName);
                itemView.setImage(info.mBmpRes);
                itemView.setTag(info.mType);

                itemView.setBackgroundColor(info.mType == mSelectedType ? Color.RED : Color.TRANSPARENT);
            }
        }

        @Override
        public int getItemCount()
        {
            return mData != null ? mData.size() : 0;
        }

        @Override
        public void onClick(View v)
        {
            Object tag = v.getTag();
            if (tag instanceof GPUTransitionFilterType && mListener != null)
            {
                mSelectedType = (GPUTransitionFilterType) tag;
                notifyDataSetChanged();
                mListener.onItemClick((GPUTransitionFilterType) tag);
            }
        }

        public void onPause()
        {
            mSelectedType = GPUTransitionFilterType.NONE;
            notifyDataSetChanged();
        }
    }

    private static class FilterItemView extends FrameLayout
    {
        private ImageView imageView;
        private TextView textView;

        public FilterItemView(Context context)
        {
            super(context);

            initUI(context);
        }

        private void initUI(Context context)
        {
            LinearLayout outsideLayout = new LinearLayout(context);
            outsideLayout.setOrientation(LinearLayout.VERTICAL);
            FrameLayout.LayoutParams fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.CENTER;
            addView(outsideLayout, fl);
            {
                imageView = new ImageView(context);
                imageView.setBackgroundColor(Color.GRAY);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(PxUtil.sU_1080p(256), PxUtil.sU_1080p(200));
                outsideLayout.addView(imageView, params);

                textView = new TextView(context);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                textView.setBackgroundColor(Color.WHITE);
                textView.setTextColor(Color.BLACK);
                textView.setGravity(Gravity.CENTER);
                params = new LinearLayout.LayoutParams(PxUtil.sU_1080p(256), PxUtil.sU_1080p(120));
                outsideLayout.addView(textView, params);
            }
        }

        public void setImage(int resId)
        {
            imageView.setImageResource(resId);
        }

        public void setFilterName(String name)
        {
            textView.setText(name);
        }
    }

    private static class FilterInfo
    {
        String mName;
        int mBmpRes;
        GPUTransitionFilterType mType;
    }
}
