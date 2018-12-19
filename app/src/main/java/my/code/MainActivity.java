package my.code;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends BaseActivity
{
    @Override
    public void onCreateBaseData()
    {

    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params)
    {

    }

    @Override
    public void onCreateFinish()
    {

    }

    public static class ItemAdapter extends RecyclerView.Adapter
    {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            Button item = new Button(parent.getContext());

            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
        {

        }

        @Override
        public int getItemCount()
        {
            return 0;
        }
    }
}
