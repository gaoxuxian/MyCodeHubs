package trunk.android;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import trunk.BaseActivity;

public class MultThreadActivity extends BaseActivity {

    private MyThread mThread;

    @Override
    public void onCreateBaseData() throws Exception {

    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params) {
        Button btn = new Button(context);
        btn.setText("开启线程");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThread = new MyThread();
                mThread.start();
            }
        });
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parent.addView(btn, params);

        btn = new Button(context);
        btn.setText("线程等待");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThread.testLock();
            }
        });
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        parent.addView(btn, params);

        btn = new Button(context);
        btn.setText("notify线程");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThread.notifyLock();
            }
        });
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END;
        parent.addView(btn, params);
    }

    @Override
    public void onCreateFinish() {

    }

    private class MyThread extends Thread {
        private volatile boolean lock = true;
        private final Object lock_obj = new Object();

        @Override
        public void run() {
            super.run();

            while (lock) {
                synchronized (this) {
                    try {
                        Log.e("***", "run: 自动 wait");
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            Log.e("***", "run: 结束");
        }

        public void notifyLock() {
            lock = false;
            synchronized (this) {
                notifyAll();
            }
        }

        public void testLock() {
            lock = true;
            synchronized (this) {
                try {
                    Log.e("***", "testLock: 手动 wait");
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
