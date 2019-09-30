package trunk.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import util.PxUtil;

import java.util.ArrayList;
import java.util.Random;

/**
 * #I# RecycleView自定义item size
 */
public class MainActivity45 extends Activity {

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private ArrayList<MyData> mArr = new ArrayList<>();
    private final int FLAG_INDEX = 25;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化构造数据
        PxUtil.init(this);
        mArr.clear();
        for (int i = 0; i < 50; i++) {
            mArr.add(new MyData());
        }
        mArr.get(FLAG_INDEX).setFlag();

        //构建UI
        FrameLayout.LayoutParams fl;
        FrameLayout fr = new FrameLayout(this);
        setContentView(fr);
        {
            TextView tex = new TextView(this);
            tex.setOnTouchListener(new View.OnTouchListener() {
                private float mX;
                private float tempX;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            mX = event.getX();
                            float offset = mRecyclerView.computeHorizontalScrollOffset();
                            System.out.println("getScrollX : " + offset);
                            break;
                        }
                        case MotionEvent.ACTION_MOVE: {
                            if (mX > -1) {
                                tempX = event.getX();
                                int offset = Math.round(tempX - mX);
                                if (offset > 0) {
                                    mRecyclerView.offsetChildrenHorizontal(offset);
                                }
                                updateData(offset);
                                mAdapter.notifyDataSetChanged();
//                                mRecyclerView.requestLayout();
                                mX = tempX;
                            } else {
                                mX = event.getX();
                            }
                            break;
                        }
                        case MotionEvent.ACTION_CANCEL:
                            mX = -1;
                            break;
                    }
                    return true;
                }
            });
            tex.setBackgroundColor(0xff909090);
            tex.setText("水平拖动此处");
            tex.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
            tex.setGravity(Gravity.CENTER);
            fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, PxUtil.sU_1080p(100));
            fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
            fr.addView(tex, fl);

            mRecyclerView = new RecyclerView(this);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(RecyclerView.HORIZONTAL);
            mRecyclerView.setLayoutManager(llm);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());

            mAdapter = new MyAdapter();
            mAdapter.setData(mArr);
            mRecyclerView.setAdapter(mAdapter);

            fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.CENTER;
            fr.addView(mRecyclerView, fl);
        }
    }

    private void updateData(int offset) {
        MyData data;
        if (offset > 0) {
            //缩小前面
            for (int i = FLAG_INDEX - 1; i > -1; i--) {
                data = mArr.get(i);
                if ((offset = updateItem(data, offset)) > 0) {
                    break;
                }
            }
        } else {
            //缩小后面
            int len = mArr.size();
            for (int i = FLAG_INDEX + 1; i < len; i++) {
                data = mArr.get(i);
                if ((offset = updateItem(data, offset)) > 0) {
                    break;
                }
            }
        }
    }

    private int updateItem(MyData data, int offset) {
        offset = Math.abs(offset);
        int re = data.w - offset;
        if (re >= 0) {
            data.w = re;
        } else {
            data.w = 0;
        }
        return re;
    }

    public static class MyData {
        public int color;
        public int w;
        public int h;
        public boolean flag;

        public MyData() {

            Random random = new Random();
            color = 0xFF000000 |
                    (random.nextInt(256) << 16) |
                    (random.nextInt(256) << 8) |
                    random.nextInt(256);
            h = PxUtil.sU_1080p(120);
            w = h + random.nextInt(PxUtil.sU_1080p(300));
        }

        public MyData setFlag() {
            w = PxUtil.sU_1080p(15);
            color = 0xff000000;
            flag = true;
            return this;
        }
    }

    public static class MyView extends View {

        private int mW;
        private int mH;

        public MyView(Context context) {
            super(context);
        }

        public MyView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public void setSize(int w, int h) {
            mW = w;
            mH = h;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(mW, mH);
            //System.out.println("onMeasure");
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private MyView mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = (MyView) itemView;
        }

        public void setData(int color, int w, int h) {
            mView.setBackgroundColor(color);
            changeSize(w, h);
        }

        public void changeSize(int w, int h) {
            mView.setSize(w, h);
        }
    }

    public static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private ArrayList<MyData> mData;

        public void setData(ArrayList<MyData> arr) {
            mData = arr;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(new MyView(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            MyData data = mData.get(position);
            holder.setData(data.color, data.w, data.h);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }
}
