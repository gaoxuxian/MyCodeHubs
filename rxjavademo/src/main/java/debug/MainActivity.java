package debug;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.example.rxjavademo.R;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onStart() {
        super.onStart();

        Observable
                .fromArray(new String[]{"1", "2", "3"})
                .subscribeOn(Schedulers.io()) // 指示订阅生成过程所在的线程
                .map(new Function<String, Integer>() {
                    @Override
                    public Integer apply(String s) throws Exception {
                        return Integer.parseInt(s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread()) // 表示观察者回调方法时的所在线程
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e("测试rxjava", integer + "，" + Thread.currentThread().getName());
                    }
                });
    }
}
