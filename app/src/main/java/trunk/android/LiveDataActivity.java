package trunk.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import trunk.BaseActivity;
import util.PxUtil;

import java.util.ArrayList;

public class LiveDataActivity extends BaseActivity {
    private RecyclerView mItemView;
    private ArrayList<MyData> mData;

    @Override
    public void onCreateBaseData() throws Exception {
        Observer<MyData> myDataObserver = new Observer<MyData>() {
            @Override
            public void onChanged(MyData myData) {
                mItemView.getAdapter().notifyDataSetChanged();
            }
        };

        mData = new ArrayList<>();

        MyData data = new MyData();
        data.setName("Number-1").setSelected(true).observeForever(myDataObserver);
        mData.add(data);

        data = new MyData();
        data.setName("Number-2").setSelected(false).observeForever(myDataObserver);
        mData.add(data);

        data = new MyData();
        data.setName("Number-3").setSelected(false).observeForever(myDataObserver);
        mData.add(data);

        data = new MyData();
        data.setName("Number-4").setSelected(false).observeForever(myDataObserver);
        mData.add(data);

        data = new MyData();
        data.setName("Number-5").setSelected(false).observeForever(myDataObserver);
        mData.add(data);

        data = new MyData();
        data.setName("Number-6").setSelected(false).observeForever(myDataObserver);
        mData.add(data);

        data = new MyData();
        data.setName("Number-7").setSelected(false).observeForever(myDataObserver);
        mData.add(data);

        data = new MyData();
        data.setName("Number-8").setSelected(false).observeForever(myDataObserver);
        mData.add(data);

        data = new MyData();
        data.setName("Number-9").setSelected(false).observeForever(myDataObserver);
        mData.add(data);

        data = new MyData();
        data.setName("Number-10").setSelected(false).observeForever(myDataObserver);
        mData.add(data);
    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params) {
        mItemView = new RecyclerView(context);
        mItemView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(mItemView, params);

        mItemView.setAdapter(new MyAdapter(mData));
    }

    @Override
    public void onCreateFinish() {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private static class MyAdapter extends RecyclerView.Adapter implements View.OnClickListener {

        private ArrayList<MyData> mData;
        private int mLastSelectedIndex = 0;

        public MyAdapter(ArrayList<MyData> data){
            mData = data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView item = new TextView(parent.getContext());
            item.setAllCaps(false);
            item.setOnClickListener(this);
            item.setGravity(Gravity.CENTER);
            item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            item.setTextColor(Color.BLACK);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxUtil.sU_1080p(300));
            item.setLayoutParams(params);
            return new RecyclerView.ViewHolder(item) {};
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            TextView itemView = (TextView) holder.itemView;
            itemView.setTag(position);
            MyData data = mData.get(position);
            itemView.setText(data.getName());
            itemView.setTextColor(data.isSelected ? Color.RED : Color.BLACK);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            mData.get(mLastSelectedIndex).setSelected(false);
            mLastSelectedIndex = position;
            mData.get(position).setSelected(true).notifyDataChange();
        }
    }

    private static class MyData extends MutableLiveData<MyData>{
        private String mName;
        private boolean isSelected;

        public void notifyDataChange() {
            setValue(this);
        }

        public MyData setName(String name){
            mName = name;
            return this;
        }

        public MyData setSelected(boolean is){
            isSelected = is;
            return this;
        }

        public String getName(){
            return mName;
        }

        public boolean isSelected(){
            return isSelected;
        }
    }
}
