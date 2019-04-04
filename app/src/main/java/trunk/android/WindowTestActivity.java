package trunk.android;

import android.graphics.Color;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import util.PxUtil;

public class WindowTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        FrameLayout layout = new FrameLayout(this);
        setContentView(layout);
        View view = new View(this)
        {
            @Override
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                Log.d("xxx", "onAttachedToWindow: view-1");
            }
        };
        view.setBackgroundColor(Color.RED);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxUtil.sU_1080p(300));
        params.gravity = Gravity.BOTTOM;
        layout.addView(view, params);

        Button btn = new Button(this);
        btn.setText("添加view");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = new View(v.getContext())
                {
                    @Override
                    protected void onAttachedToWindow() {
                        super.onAttachedToWindow();
                        Log.d("xxx", "onAttachedToWindow: view-2");
                    }
                };
                view.setBackgroundColor(Color.BLUE);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxUtil.sU_1080p(300));
                layout.addView(view, params);
            }
        });
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PxUtil.sU_1080p(300));
        layout.addView(btn, params);
    }

    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(option);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        getWindow().setAttributes(attributes);
    }
}
