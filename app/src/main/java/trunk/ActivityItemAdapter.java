package trunk;

import android.content.Intent;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import util.PxUtil;

import java.util.ArrayList;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/19.
 */
public class ActivityItemAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    public interface Listener {
        void onClickItem(Intent intent);
    }

    public @interface DataKey {
        int ITEM_TITLE = 1;
        int CLASS_INTENT = 2;
    }

    ArrayList<SparseArray<Object>> mData;
    private Listener mListener;

    public ActivityItemAdapter(@NonNull ArrayList<SparseArray<Object>> data, Listener listener) {
        mData = data;
        mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Button item = new Button(parent.getContext());
        item.setAllCaps(false);
        item.setOnClickListener(this);
        item.setGravity(Gravity.CENTER);
        ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(PxUtil.sU_1080p(360), PxUtil.sV_1080p(300));
        item.setLayoutParams(params);
        return new RecyclerView.ViewHolder(item) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Button itemView = (Button) holder.itemView;
        itemView.setTag(position);
        SparseArray<Object> map = mData.get(position);
        itemView.setText((CharSequence) map.get(DataKey.ITEM_TITLE));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        SparseArray<Object> map = mData.get(position);
        Intent intent = (Intent) map.get(DataKey.CLASS_INTENT);
        if (mListener != null) {
            mListener.onClickItem(intent);
        }
    }

    public void destroy() {
        mListener = null;
        mData = null;
    }
}
