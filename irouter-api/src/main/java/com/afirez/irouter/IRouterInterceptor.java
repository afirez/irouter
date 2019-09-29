package com.afirez.irouter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.afirez.irouter.exception.RouteNotFoundException;
import com.afirez.rxactivityresult.AResult;
import com.afirez.rxactivityresult.RxActivityResult;
import com.afirez.spi.ExtensionLoader;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import java.io.IOException;
import java.lang.reflect.Method;

class IRouterInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        final Request request = chain.request();

        Observable<Result> rxResult;
        rxResult = handleServiceNavIfNeeded(request);
        if (rxResult == null) {
            rxResult = handleFragmentNavIfNeeded(request);
        }
        if (rxResult == null) {
            rxResult = handleActivityNavIfNeeded(request);
        }

        if (rxResult == null) {
            final String path = request.path();
            final String uri = request.uri();
            final String action = request.action();
            String msg = "Route for { path = " + path + ", uri = " + uri + ", action = " + action + " } not found";
            RouteNotFoundException error = new RouteNotFoundException(msg);
            rxResult = Observable.error(error);
        }

        // log
        rxResult = rxResult.doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                IRouter.log("----> onSubscribe: " + request);
            }
        }).doOnNext(new Consumer<Result>() {
            @Override
            public void accept(Result result) throws Exception {
                IRouter.log("<---- onNext result: " + result);
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                IRouter.log("<---- onComplete call: " + request);
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                IRouter.log("<---- onError: " + throwable);
            }
        }).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                IRouter.log("<---- onDispose: " + request);
            }
        });

        return Response.create(request, rxResult);
    }


    private Observable<Result> handleServiceNavIfNeeded(final Request request) {
        Observable<Result> rxResult = null;
        final String path = request.path();
        if (path != null && !path.isEmpty()) {
            final Class<?> target = ExtensionLoader.getInstance().extension(path);
            final Class<?> fragmentType = ExtensionLoader.fragmentType();
            if (target != null
                    && !Activity.class.isAssignableFrom(target)
                    && !((fragmentType != null && fragmentType.isAssignableFrom(target)))) {
                request.call = Request.CALL_SERVICE;

                rxResult = Observable.create(new ObservableOnSubscribe<Result>() {
                    @Override
                    public void subscribe(ObservableEmitter<Result> emitter) throws Exception {
                        try {
                            Object obj = ExtensionLoader.getInstance().loadExtension(path);
                            emitter.onNext(new Result(0, Activity.RESULT_OK, obj));
                            emitter.onComplete();
                        } catch (Throwable throwable) {
                            IRouter.log("<---- error: " + throwable);
                            emitter.tryOnError(throwable);
                        }
                    }
                });
            }
        }
        return rxResult;
    }


    private Observable<Result> handleFragmentNavIfNeeded(final Request request) {
        Observable<Result> rxResult = null;
        final String path = request.path();
        if (path != null && !path.isEmpty()) {
            final Class<?> target = ExtensionLoader.getInstance().extension(path);
            final Class<?> fragmentType = ExtensionLoader.fragmentType();
            if (target != null &&
                    ((fragmentType != null && fragmentType.isAssignableFrom(target)))
            ) {
                request.call = Request.CALL_FRAGMENT;

                rxResult = Observable.create(new ObservableOnSubscribe<Result>() {
                    @Override
                    public void subscribe(ObservableEmitter<Result> emitter) throws Exception {
                        Object obj;
                        try {
                            obj = target.newInstance();
                            Intent intent = request.intent();
                            Bundle extras = intent.getExtras();
                            if (extras != null) {
                                Method setArguments = fragmentType.getDeclaredMethod("setArguments", Bundle.class);
                                setArguments.setAccessible(true);
                                setArguments.invoke(obj, extras);
                            }
                            emitter.onNext(new Result(0, Activity.RESULT_OK, obj));
                            emitter.onComplete();
                        } catch (Throwable throwable) {
                            IRouter.log("<---- error: " + throwable);
                            emitter.tryOnError(throwable);
                        }
                    }
                });
            }
        }
        return rxResult;
    }

    private Observable<Result> handleActivityNavIfNeeded(final Request request) {
        Observable<Result> rxResult = null;
        final String path = request.path();
        if (path != null && !path.isEmpty()) {
            final Class<?> target = ExtensionLoader.getInstance().extension(path);
            if (target != null && Activity.class.isAssignableFrom(target)) {
                request.call = Request.CALL_ACTIVITY;

                rxResult = RxActivityResult.topActivity.rxTopActivity()
                        .take(1)
                        .flatMap(new Function<Activity, ObservableSource<AResult>>() {
                            @Override
                            public ObservableSource<AResult> apply(Activity activity) throws Exception {
                                Intent intent = new Intent(request.intent());
                                intent.setClass(activity, target);
                                return RxActivityResult.with(activity).startIntent(intent);
                            }
                        }).map(new Function<AResult, Result>() {
                            @Override
                            public Result apply(AResult aResult) throws Exception {
                                return new Result(aResult.requestCode(), aResult.requestCode(), aResult.data());
                            }
                        });

            }
        }

        final String uri = request.uri();
        final String action = request.action();
        if ((uri != null && !uri.isEmpty()) || (action != null && !action.isEmpty())) {
            request.call = Request.CALL_ACTIVITY;

            final Intent intent = new Intent(request.intent());
            if (uri != null && !uri.isEmpty()) {
                intent.setData(Uri.parse(uri));
            }

            if (action != null && !action.isEmpty()) {
                intent.setAction(action);
            }

            rxResult = RxActivityResult.topActivity.rxTopActivity()
                    .take(1)
                    .flatMap(new Function<Activity, ObservableSource<AResult>>() {
                        @Override
                        public ObservableSource<AResult> apply(Activity activity) throws Exception {
                            return RxActivityResult.with(activity).startIntent(intent);
                        }
                    }).map(new Function<AResult, Result>() {
                        @Override
                        public Result apply(AResult aResult) throws Exception {
                            return new Result(aResult.requestCode(), aResult.requestCode(), aResult.data());
                        }
                    });
        }

        return rxResult;
    }
}
