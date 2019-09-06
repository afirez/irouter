package com.afirez.rxactivityresult;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class TopActivity {
    final Application application;

    private ArrayList<Activity> activities = new ArrayList<>();

    volatile Activity topActivityOrNull;
    Application.ActivityLifecycleCallbacks activityLifecycleCallbacks;

    public TopActivity(Application application) {
        this.application = application;
        registerActivityLifeCycle();
    }

    private void registerActivityLifeCycle() {
        if (activityLifecycleCallbacks != null)
            application.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);

        activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                topActivityOrNull = activity;
                activities.add(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                topActivityOrNull = activity;
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (activity == topActivityOrNull) {
                    topActivityOrNull = null;
                }
                activities.remove(activity);
            }
        };

        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    public Activity topActivityOrNull() {
        return topActivityOrNull;
    }

    /**
     * Emits just one time a valid reference to the current activity
     *
     * @return the current activity
     */
    volatile boolean emitted = false;

    public Observable<Activity> rxTopActivity() {
        emitted = false;
        return Observable.interval(50, 50, TimeUnit.MILLISECONDS)
                .map(new Function<Long, Object>() {
                    @Override
                    public Object apply(Long aLong) throws Exception {
                        Activity[] theActivities = activities.toArray(new Activity[activities.size()]);
                        if (theActivities != null) {
                            int length = theActivities.length;
                            for (int i = length - 1; i >= 0; i--) {
                                Activity activity = theActivities[i];
                                if (activity != null
                                        && !activity.isFinishing()
                                        && !activity.isDestroyed()) {
                                    return activity;
                                } else {
                                    theActivities[i] = null;
                                }
                            }
                        }
                        return 0; // no available activity
                    }
                })
                .takeWhile(new Predicate<Object>() {
                    @Override
                    public boolean test(Object candidate) throws Exception {
                        boolean continueEmitting = true;
                        if (emitted) continueEmitting = false;
                        if (candidate instanceof Activity) emitted = true;
                        return continueEmitting;
                    }
                })
                .filter(new Predicate<Object>() {
                    @Override
                    public boolean test(Object candidate) throws Exception {
                        return candidate instanceof Activity;
                    }
                })
                .map(new Function<Object, Activity>() {
                    @Override
                    public Activity apply(Object activity) throws Exception {
                        return (Activity) activity;
                    }
                });
    }

}
