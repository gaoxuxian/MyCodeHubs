package debug;

import com.xx.commonlib.BaseApplication;
import com.xx.commonlib.ThreadUtil;

public class MyApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        ThreadUtil.init();
    }
}
