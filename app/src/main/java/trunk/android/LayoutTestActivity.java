package trunk.android;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import trunk.BaseActivity;
import util.PxUtil;

public class LayoutTestActivity extends BaseActivity {


    @Override
    public void onCreateBaseData() throws Exception {

    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params) {
        ConstraintLayout layout = new ConstraintLayout(context);
        layout.setId(View.generateViewId());
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(layout, params);
        {
            Button btn1 = new Button(context);
            btn1.setId(View.generateViewId());
            btn1.setAllCaps(false);
            btn1.setText("button_1");
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    btn1.setVisibility(View.GONE);
                    btn1.animate().withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            btn1.setVisibility(View.GONE);
                        }
                    }).alpha(0).setDuration(300).start();
                }
            });
            ConstraintLayout.LayoutParams cllp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cllp.bottomToBottom = layout.getId();
            cllp.topToTop = layout.getId();
//            cllp.leftToLeft = layout.getId();
            cllp.rightToRight = layout.getId();
            cllp.rightMargin = PxUtil.sU_1080p(200);
            layout.addView(btn1, cllp);

            Button btn2 = new Button(context);
            btn2.setId(View.generateViewId());
            btn2.setAllCaps(false);
            btn2.setText("button_2");
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    btn1.setVisibility(View.VISIBLE);
                    btn1.animate().withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            btn1.setVisibility(View.VISIBLE);
                        }
                    }).alpha(1).setDuration(300).start();
                }
            });
            cllp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cllp.bottomToBottom = layout.getId();
            cllp.rightToLeft = btn1.getId();
            layout.addView(btn2, cllp);

            Button btn3 = new Button(context);
            btn3.setId(View.generateViewId());
            btn3.setAllCaps(false);
            btn3.setText("button_3");
            btn3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Test test1 = null;
                    Test test2 = null;
                    test1 = new Test();
                    test2 = test1;
                    test1 = new Test();
                    Log.d("xxx", "onClick: test1 = " + test1);
                    Log.d("xxx", "onClick: test2 = " + test2);
                }
            });
            cllp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cllp.bottomToTop = btn1.getId();
            cllp.leftToLeft = btn2.getId();
            layout.addView(btn3, cllp);
        }
    }

    @Override
    public void onCreateFinish() {

    }

    public static class Test {
        public static final ObjectTest LOCK = new ObjectTest();
    }

    public static class ObjectTest {
        public ObjectTest() {
            Log.d("xxx", "ObjectTest: ");
        }
    }
}
