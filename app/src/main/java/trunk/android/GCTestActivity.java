package trunk.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * GC Root 对象
 *
 * 虚拟机栈（栈帧中的本地变量表）中引用的对象
 *
 * 方法区中类静态属性引用的对象
 *
 * 方法区中常量引用的对象
 *
 * 本地方法栈中 JNI（即一般说的 Native 方法）引用的对象
 */
public class GCTestActivity extends Activity {

    static InnerClass sInnerClass;
    InnerClass innerClass;
    static Context sContext;
    private class InnerClass {
        Context context;

        InnerClass(Context context) {
            this.context = context;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // case 1
//        sInnerClass = new InnerClass(this);
//        finish();// 内存泄漏，静态属性属于 GC Root 对象 （泄漏 GCTestActivity 实例对象、InnerClass 实例对象）

        // case 2
//        sContext = this;
//        finish(); // 同理 1（泄漏 GCTestActivity 实例对象）

        // case 3
//        innerClass = new InnerClass(this);
//        finish(); // 没有内存泄漏，类的成员属性，跟类的实例对象都存放在堆中，这里是 InnerClass 和 GCTestActivity 互相引用，但没有触及 GC Root 对象
    }
}
