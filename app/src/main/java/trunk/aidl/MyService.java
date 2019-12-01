package trunk.aidl;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;

import my.code.IService;
import my.code.myservice.aidl.Book;
import my.code.myservice.aidl.IMyService;

public class MyService extends Service {

    private final String TAG = "xxx";

    private final IMyService.Stub mBinder = new BookBinder();
    private final IService.Stub binder = new ServiceImpl();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: create 服务" + this);
        Log.d(TAG, "onCreate: thread == " + Thread.currentThread().getName());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: startCommand 服务" + this);
        Log.d(TAG, "onStartCommand: thread == " + Thread.currentThread().getName());
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: bind 服务" + this);
        Log.d(TAG, "onBind: thread == " + Thread.currentThread().getName());
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: unBind 服务" + this);
        Log.d(TAG, "onUnbind: thread == " + Thread.currentThread().getName());
        return super.onUnbind(intent);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        Log.d(TAG, "unbindService: unbindService" + this);
        Log.d(TAG, "unbindService: thread == " + Thread.currentThread().getName());
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: destroy 服务" + this);
        Log.d(TAG, "onDestroy: thread == " + Thread.currentThread().getName());
        super.onDestroy();
        Process.killProcess(Process.myPid());
    }

    public class ServiceImpl extends IService.Stub {

        @Override
        public IBinder query(int id) throws RemoteException {
            // 通过一个共用的 IBinder 根据不同的业务需求，提供多个 IBinder 实例
            Log.d("xxx", "query: id == " + 0);
            return mBinder;
        }
    }

    public static class BookBinder extends IMyService.Stub {

        @Override
        public List<Book> getBookList() throws RemoteException {
            return null;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            Log.d("xxx", "addBook: book object == " + book);
            Log.d("xxx", "addBook: book name == " + book.getName());
            Log.d("xxx", "addBook: book number == " + book.getNumber());
        }
    }
}
