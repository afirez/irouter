package com.afirez.irouter.app;

import android.app.Application;
import android.content.Context;
import com.afirez.applike.AppDelegate;
import com.afirez.irouter.IRouter;
import com.afirez.rxactivityresult.RxActivityResult;

/**
 * https://github.com/afirez/irouter
 *
 * irouter init
 */
public class IRouterApp extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        AppDelegate.getInstance().attachBaseContext(this, base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppDelegate.getInstance().onCreate(this);
        RxActivityResult.init(this);
        IRouter.setDebug(true);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        AppDelegate.getInstance().onTerminate(this);
    }
}
