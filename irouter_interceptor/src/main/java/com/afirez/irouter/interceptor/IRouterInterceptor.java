package com.afirez.irouter.interceptor;

import android.util.Log;
import com.afirez.irouter.Interceptor;
import com.afirez.irouter.Response;
import com.afirez.irouter.Result;
import com.afirez.spi.SPI;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


@SPI
public class IRouterInterceptor implements Interceptor {

    private static int temp = 0;

    @Override
    public Response intercept(final Chain chain) throws IOException {
        String path = "/irouter/activity/nav/fragment";
        Log.e("IRouterInterceptor", "intercept: " + chain.request().path());
        if (path.equals(chain.request().path())) {
            Log.e("IRouterInterceptor", "intercept: ");
            final Observable<Result> rxResult =
                    Observable.timer(2, TimeUnit.SECONDS)
                            .flatMap(new Function<Long, ObservableSource<Result>>() {
                                @Override
                                public ObservableSource<Result> apply(Long s) throws Exception {
                                    temp++;
                                    if (temp % 2 == 0) {
                                        Log.e("IRouterInterceptor", "intercept: mock no error");
                                        return chain.proceed(chain.request()).rxResult();
                                    }

                                    Log.e("IRouterInterceptor", "intercept: mock error");
                                    return Observable.error(new IllegalStateException("temp = " + temp));
                                }
                            }).subscribeOn(Schedulers.io());
            return Response.create(chain.request(), rxResult);
        }

        return chain.proceed(chain.request());
    }
}
