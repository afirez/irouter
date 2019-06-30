package com.afirez.rxactivityresult;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import java.util.concurrent.TimeUnit;

public class TopActivity {
    final Application application;
    volatile Activity topActivityOrNull;
    Application.ActivityLifecycleCallbacks activityLifecycleCallbacks;

    public TopActivity(Application application) {
        this.application = application;
        registerActivityLifeCycle();
    }

    private void registerActivityLifeCycle() {
        if (activityLifecycleCallbacks != null) application.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);

        activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
            @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                topActivityOrNull = activity;
            }

            @Override public void onActivityStarted(Activity activity) {}

            @Override public void onActivityResumed(Activity activity) {
                Log.w("TopActivity", "onActivityResumed: " + activity);
                topActivityOrNull = activity;
            }

            @Override public void onActivityPaused(Activity activity) {
                Log.w("TopActivity", "onActivityPaused: " + activity);
                topActivityOrNull = null;
            }

            @Override public void onActivityStopped(Activity activity) {}

            @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

            @Override public void onActivityDestroyed(Activity activity) {}
        };

        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    public Activity topActivityOrNull() {
        return topActivityOrNull;
    }

    /**
     * Emits just one time a valid reference to the current activity
     * @return the current activity
     */
    volatile boolean emitted = false;

    public Observable<Activity> rxTopActivity() {
        emitted = false;
        return Observable.interval(50, 50, TimeUnit.MILLISECONDS)
                .map(new Function<Long, Object>() {
                    @Override public Object apply(Long aLong) throws Exception {
                        if (topActivityOrNull == null) return 0;
                        return topActivityOrNull;
                    }
                })
                .takeWhile(new Predicate<Object>() {
                    @Override public boolean test(Object candidate) throws Exception {
                        boolean continueEmitting = true;
                        if (emitted) continueEmitting = false;
                        if (candidate instanceof Activity) emitted = true;
                        return continueEmitting;
                    }
                })
                .filter(new Predicate<Object>() {
                    @Override public boolean test(Object candidate) throws Exception {
                        return candidate instanceof Activity;
                    }
                })
                .map(new Function<Object, Activity>() {
                    @Override public Activity apply(Object activity) throws Exception {
                        return (Activity) activity;
                    }
                });
    }

}
