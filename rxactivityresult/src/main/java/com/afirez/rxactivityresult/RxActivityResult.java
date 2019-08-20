package com.afirez.rxactivityresult;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;


public final class RxActivityResult {
    public static TopActivity topActivity;

    private RxActivityResult() {
    }

    public static void init(final Application application) {
        topActivity = new TopActivity(application);
    }

    public static Builder with(Object host) {
        return new Builder(host);
    }


    public static class Builder {
        final WeakReference<Object> host;
        final Class clazz;
        final PublishSubject<AResult> subject = PublishSubject.create();
        private final boolean uiTargetActivity;

        public Builder(Object host) {
            if (topActivity == null) {
                throw new IllegalStateException("You must call RxActivityResult.init(application) before attempting to use startIntent");
            }
            this.host = new WeakReference<>(host);
            this.clazz = host.getClass();
            this.uiTargetActivity = host instanceof Activity;
        }

        public Observable<AResult> startIntentSender(IntentSender intentSender, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) {
            return startIntentSender(intentSender, fillInIntent, flagsMask, flagsValues, extraFlags, null);
        }

        public Observable<AResult> startIntentSender(IntentSender intentSender, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) {
            RequestIntentSender requestIntentSender = new RequestIntentSender(intentSender, fillInIntent, flagsMask, flagsValues, extraFlags, options);
            return startHolderActivity(requestIntentSender, null);
        }

        public Observable<AResult> startIntentByActivity(final Intent intent) {
            return startIntentByActivity(intent, null);
        }

        public Observable<AResult> startIntentByActivity(final Intent intent, OnPreResult onPreResult) {
            return startHolderActivity(new Request(intent), onPreResult);
        }

        private Observable<AResult> startHolderActivity(Request request, OnPreResult onPreResult) {

            OnResult onResult = uiTargetActivity ? onResultActivity() : onResultActivity();
            request.setOnResult(onResult);
            request.setOnPreResult(onPreResult);

            HolderActivity.setRequest(request);

            Disposable disposable = topActivity.rxTopActivity().subscribe(new Consumer<Activity>() {
                @Override
                public void accept(Activity activity) throws Exception {
                    activity.startActivity(new Intent(activity, HolderActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                }
            });

            return subject;
        }

        private OnResult onResultActivity() {
            return new OnResult() {
                @Override
                public void onResult(int requestCode, int resultCode, Intent data) {
                    Activity topActivityOrNull = topActivity.topActivityOrNull();
                    if (topActivityOrNull == null) return;

                    //If true it means some other activity has been stacked as a secondary process.
                    //Wait until the current activity be the target activity
                    if (uiTargetActivity && topActivityOrNull.getClass() != clazz) {
                        return;
                    }

                    subject.onNext(new AResult(requestCode, resultCode, data));
                    subject.onComplete();
                }

                @Override
                public void onError(Throwable throwable) {
                    subject.onError(throwable);
                }
            };
        }

//        private OnResult onResultFragment() {
//            return new OnResult() {
//                @Override
//                public void onResult(int requestCode, int resultCode, Intent data) {
//                    if (topActivity.topActivityOrNull() == null) return;
//
//                    Activity activity = topActivity.topActivityOrNull();
//
//                    FragmentActivity fragmentActivity = (FragmentActivity) activity;
//                    FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
//
//                    Fragment targetFragment = getTargetFragment(fragmentManager.getFragments());
//
//                    if (targetFragment != null) {
//                        subject.onNext(new AResult<>((T) targetFragment, requestCode, resultCode, data));
//                        subject.onComplete();
//                    }
//
//                    //If code reaches this point it means some other activity has been stacked as a secondary process.
//                    //Do nothing until the current activity be the target activity to get the associated fragment
//                }
//
//                @Override
//                public void onError(Throwable throwable) {
//                    subject.onError(throwable);
//                }
//            };
//        }
//
//        @Nullable
//        Fragment getTargetFragment(List<Fragment> fragments) {
//            if (fragments == null) return null;
//
//            for (Fragment fragment : fragments) {
//                if (fragment != null && fragment.isVisible() && fragment.getClass() == clazz) {
//                    return fragment;
//                } else if (fragment != null && fragment.isAdded() && fragment.getChildFragmentManager() != null) {
//                    List<Fragment> childFragments = fragment.getChildFragmentManager().getFragments();
//                    Fragment candidate = getTargetFragment(childFragments);
//                    if (candidate != null) return candidate;
//                }
//            }
//
//            return null;
//        }

        public Observable<AResult> startIntent(final Intent intent) {
            return topActivity.rxTopActivity()
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(new Function<Activity, ObservableSource<AResult>>() {
                        @Override
                        public ObservableSource<AResult> apply(Activity activity) throws Exception {
                            final int requestCode = requestCode();
                            try {
                                HolderFragment.startActivityForResult(activity, intent, requestCode);
                            } catch (Throwable e) {
                                return Observable.error(e);
                            }
                            return HolderFragment.with(activity.getFragmentManager())
                                    .rxActivityResult()
                                    .filter(new Predicate<AResult>() {
                                        @Override
                                        public boolean test(AResult aResult) throws Exception {
                                            return aResult.requestCode() == requestCode;
                                        }
                                    });
                        }
                    });
        }
    }

    private static AtomicInteger requestCode = new AtomicInteger(65535);

    private static int requestCode() {
        requestCode.compareAndSet(0, 65535);
        return requestCode.getAndDecrement();
    }
}
