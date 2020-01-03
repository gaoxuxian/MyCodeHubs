package trunk.java;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.xx.javademo.IO.IOCase1;

import trunk.BaseActivity;

public class IOTest1Activity extends BaseActivity {

    @Override
    public void onCreateBaseData() throws Exception {

    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params) {
        Button btn = new Button(context);
        btn.setText("测试写");
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IOCase1 io = new IOCase1();
                io.init(v.getContext());
                io.writeTestStringToSD();
            }
        });
        parent.addView(btn, params);

        btn = new Button(context);
        btn.setText("测试读");
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IOCase1 io = new IOCase1();
                io.init(v.getContext());
                io.readSDTestString();
            }
        });
        parent.addView(btn, params);
    }

    @Override
    public void onCreateFinish() {

    }
}
