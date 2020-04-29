package trunk.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import my.code.IService;
import my.code.Book;
import my.code.IMyService;
import trunk.BaseActivity;
import trunk.aidl.MyService;

public class ServiceTestActivity extends BaseActivity {

    private ServiceConnection mServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateBaseData() throws Exception {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("xxx", "onServiceConnected: ");
                Log.d("xxx", "onServiceConnected: run on Pid-" + Process.myPid() + ", Uid-" + Process.myUid() +", " + Thread.currentThread().getName());
                IService iService = IService.Stub.asInterface(service);
                try {
                    IBinder binder = iService.query(0);
                    IMyService iMyService = IMyService.Stub.asInterface(binder);
                    iMyService.addBook(new Book("小明", 10));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("xxx", "onServiceDisconnected: ");
            }
        };
    }

    @Override
    public void onCreateChildren(Context context, FrameLayout parent, FrameLayout.LayoutParams params) {
        ConstraintLayout layout = new ConstraintLayout(context);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.addView(layout, params);
        {
            Button btn = new Button(context);
            btn.setId(View.generateViewId());
            btn.setText("开启服务");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
                            Intent intent = new Intent(ServiceTestActivity.this, MyService.class);
                            startService(intent);
//                        }
//                    }).start();
                }
            });
            ConstraintLayout.LayoutParams cllp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cllp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            cllp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
            cllp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
            cllp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            layout.addView(btn, cllp);

            Button btn1 = new Button(context);
            btn1.setId(View.generateViewId());
            btn1.setText("关闭服务");
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ServiceTestActivity.this, MyService.class);
                    stopService(intent);
                }
            });
            cllp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cllp.leftToLeft = btn.getId();
            cllp.topToBottom = btn.getId();
            layout.addView(btn1, cllp);

            Button btn2 = new Button(context);
            btn2.setId(View.generateViewId());
            btn2.setText("绑定服务");
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ServiceTestActivity.this, MyService.class);
                    bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
                }
            });
            cllp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cllp.leftToRight = btn.getId();
            cllp.topToTop = btn.getId();
            layout.addView(btn2, cllp);

            Button btn3 = new Button(context);
            btn3.setId(View.generateViewId());
            btn3.setText("解绑服务");
            btn3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unbindService(mServiceConnection);
                }
            });
            cllp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cllp.leftToLeft = btn2.getId();
            cllp.topToBottom = btn2.getId();
            layout.addView(btn3, cllp);
        }
    }

    @Override
    public void onCreateFinish() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
