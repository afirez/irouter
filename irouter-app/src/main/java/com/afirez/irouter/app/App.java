package com.afirez.irouter.app;

import android.app.Application;
import com.afirez.irouter.IRouter;
import com.afirez.rxactivityresult.RxActivityResult;

/**
 * https://github.com/afirez/irouter
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RxActivityResult.init(this);
        IRouter.setDebug(true);
    }
}
