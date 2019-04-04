package trunk.service;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;

public class ServiceTestActivityService extends Service {

    private final String TAG = getClass().getName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: create 服务" + this);
        Log.d(TAG, "onCreate: thread == " + Thread.currentThread().getName());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: startCommand 服务" + this);
        Log.d(TAG, "onCreate: thread == " + Thread.currentThread().getName());
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: bind 服务" + this);
        Log.d(TAG, "onCreate: thread == " + Thread.currentThread().getName());
        return new MyBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: unBind 服务" + this);
        Log.d(TAG, "onCreate: thread == " + Thread.currentThread().getName());
        return super.onUnbind(intent);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        Log.d(TAG, "unbindService: unbindService" + this);
        Log.d(TAG, "onCreate: thread == " + Thread.currentThread().getName());
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: destroy 服务" + this);
        Log.d(TAG, "onCreate: thread == " + Thread.currentThread().getName());
        super.onDestroy();
    }

    public class MyBinder extends Binder {
        public void stopService(ServiceConnection serviceConnection) {
            unbindService(serviceConnection);
        }
    }
}
