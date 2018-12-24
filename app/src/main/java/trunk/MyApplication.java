package trunk;

import android.app.Application;
import util.GLThreadPool;
import util.PxUtil;
import util.SysConfig;
import util.ThreadUtil;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/19.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate()
    {
        super.onCreate();
        PxUtil.init(this);
        ThreadUtil.init();
        GLThreadPool.init();

        SysConfig.setDebugMode(true);
    }
}
