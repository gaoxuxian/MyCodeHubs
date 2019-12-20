package trunk.java;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.xx.javademo.IO.IOCase1;
import com.xx.javademo.IO.IOCase2;

import trunk.BaseActivity;
import util.PxUtil;

public class IOTest2Activity extends BaseActivity {

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
            btn.setText("测试 FileChannel 写");
            ConstraintLayout.LayoutParams cp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            cp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IOCase2 io = new IOCase2();
                    io.writeContentToFile();
                }
            });
            layout.addView(btn, cp);

            btn = new Button(context);
            btn.setId(2);
            btn.setAllCaps(false);
            btn.setText("测试 FileWriter 写");
            cp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            cp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            cp.topToBottom = 1;
            cp.topMargin = PxUtil.sV_1080p(30);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IOCase2 io = new IOCase2();
                    io.writeContentToFileV2();
                }
            });
            layout.addView(btn, cp);

            btn = new Button(context);
            btn.setId(3);
            btn.setAllCaps(false);
            btn.setText("测试 FileChannel 读");
            cp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            cp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            cp.topToBottom = 2;
            cp.topMargin = PxUtil.sV_1080p(30);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IOCase2 io = new IOCase2();
                    io.readContentFromFile();
                }
            });
            layout.addView(btn, cp);

            btn = new Button(context);
            btn.setId(4);
            btn.setText("测试FileChannel copy");
            btn.setAllCaps(false);
            cp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            cp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            cp.topToBottom = 3;
            cp.topMargin = PxUtil.sV_1080p(30);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IOCase2 io = new IOCase2();
                    io.copyContentToNewFile();
                }
            });
            layout.addView(btn, cp);

            btn = new Button(context);
            btn.setText("测试FileReader/FileWriter copy");
            btn.setAllCaps(false);
            cp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            cp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            cp.topToBottom = 4;
            cp.topMargin = PxUtil.sV_1080p(30);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IOCase2 io = new IOCase2();
                    io.copyContentToNewFileV2();
                }
            });
            layout.addView(btn, cp);
        }
    }

    @Override
    public void onCreateFinish() {

    }
}
