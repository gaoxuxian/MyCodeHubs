package trunk.java;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.xx.commonlib.PxUtil;

import trunk.BaseActivity;

public class GCRootTestActivity extends BaseActivity {

    @Override
    public void onCreateBaseData() throws Exception {
        PxUtil.init(this);
    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params) {
        ConstraintLayout layout = new ConstraintLayout(context);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(layout, params);
        {
            Button btn = new Button(context);
            btn.setId(1);
            btn.setAllCaps(false);
            btn.setText("成员变量inner = new Inner()");
            ConstraintLayout.LayoutParams cp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            cp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inner = new Inner();
                }
            });
            layout.addView(btn, cp);

            btn = new Button(context);
            btn.setId(2);
            btn.setAllCaps(false);
            btn.setText("inner.setContext(this)");
            cp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            cp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            cp.topToBottom = 1;
            cp.topMargin = PxUtil.sV_1080p(30);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (inner != null) {
                        inner.setContext(GCRootTestActivity.this);
                    }
                }
            });
            layout.addView(btn, cp);

            btn = new Button(context);
            btn.setId(3);
            btn.setAllCaps(false);
            btn.setText("static变量 sInner = new Inner()");
            cp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            cp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            cp.topToBottom = 2;
            cp.topMargin = PxUtil.sV_1080p(30);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sInner = new Inner();
                }
            });
            layout.addView(btn, cp);

            btn = new Button(context);
            btn.setId(4);
            btn.setText("sInner.setContext(this)");
            btn.setAllCaps(false);
            cp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            cp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            cp.topToBottom = 3;
            cp.topMargin = PxUtil.sV_1080p(30);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sInner != null) {
                        sInner.setContext(GCRootTestActivity.this);
                    }
                }
            });
            layout.addView(btn, cp);
        }
    }

    Inner inner;// 没有触发 GC Root
    static Inner sInner; // 静态变量是 GC Root
    @Override
    public void onCreateFinish() {}

    private class Inner {
        Context context;

        public void setContext(Context context) {
            this.context = context;
        }
    }
}
