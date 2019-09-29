package com.afirez.irouter;

import io.reactivex.Observable;
import io.reactivex.observers.DefaultObserver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class DefaultCallAdapterFactory extends CallAdapter.Factory {

    public DefaultCallAdapterFactory() {
    }

    @Override
    public CallAdapter<?, ?> get(final Type returnType, Annotation[] annotations, IRouter iRouter) {
        final Class<?> rawType = getRawType(returnType);
        Type responseType = null;
        try {
            responseType = Utils.getCallResponseType(returnType);
        } catch (Throwable ignore) {
//            IRouter.log("----> error: " + ignore);
        }

        //return Observable<Result> for result
        final boolean returnRxResult = responseType == Result.class;
        if ((rawType == Observable.class)) {
            final Type finalResponseType = responseType;
            return new CallAdapter<Response, Observable<Result>>() {
                @Override
                public Type responseType() {
                    return returnType;
                }

                @Override
                public Observable<Result> adapt(Call<Response> call) {
                    try {
                        Response response = call.execute();
                        Observable<Result> rxResult = response.rxResult();

                        if (rxResult == null) {
                            return Observable.error(new NullPointerException("response.rxResult == null"));
                        }

                        if (!returnRxResult) {
                            return Observable.error(new IllegalStateException("Illegal responseType " + finalResponseType));
                        }

                        return rxResult;
                    } catch (Throwable e) {
                        IRouter.log("----> error: " + e);
                        return Observable.error(e);
                    }
                }
            };
        }

        //return non Observable<Result>
        return new CallAdapter<Response, Object>() {
            @Override
            public Type responseType() {
                return returnType;
            }

            @Override
            public Object adapt(Call<Response> call) {
                Response response;
                try {
                    response = call.execute();

                    Observable<Result> rxResult = response.rxResult();

                    if (response.request().call == Request.CALL_FRAGMENT) {
                        Object fragment = rxResult.blockingFirst().data();
                        if (rawType.isAssignableFrom(fragment.getClass())) {
                            return fragment;
                        }
                    }

                    if (response.request().call == Request.CALL_SERVICE) {
                        Object service = rxResult.blockingFirst().data();
                        if (rawType.isAssignableFrom(service.getClass())) {
                            return service;
                        }
                    }

                    // response.request().call == Request.CALL_ACTIVITY
                    // or response.request().call == Request.CALL_UNKNOWN
                    rxResult.subscribe(new DefaultObserver<Result>() {
                        @Override
                        public void onNext(Result result) {
                            IRouter.log("----> missing result: " + result);
                        }

                        @Override
                        public void onError(Throwable e) {
                            IRouter.log("----> missing result error: " + e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
                } catch (Throwable e) {
                    IRouter.log("----> error: " + e);
                }


                if (rawType == Byte.TYPE) {
                    return (byte) 0;
                } else if (rawType == Short.TYPE) {
                    return (short) 0;
                } else if (rawType == Integer.TYPE) {
                    return 0;
                } else if (rawType == Long.TYPE) {
                    return 0L;
                } else if (rawType == Float.TYPE) {
                    return 0.0f;
                } else if (rawType == Double.TYPE) {
                    return 0.0;
                } else if (rawType == Boolean.TYPE) {
                    return false;
                }

                return null;
            }
        };
    }
}
