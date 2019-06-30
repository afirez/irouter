package com.afirez.irouter.app;

import android.app.Application;
import com.afirez.rxactivityresult.RxActivityResult;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RxActivityResult.init(this);
    }
}
