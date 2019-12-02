package trunk;

import android.app.Application;
import com.xx.avlibrary.gl.util.GLThreadPool;
import com.xx.avlibrary.gl.util.SysConfig;
import util.PxUtil;
import util.ThreadUtil;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/19.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate()
    {
        Thread.dumpStack();
        super.onCreate();
        PxUtil.init(this);
        ThreadUtil.init();
        GLThreadPool.init();

        SysConfig.setDebugMode(true);
    }
}
