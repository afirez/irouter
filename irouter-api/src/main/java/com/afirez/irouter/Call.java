package com.afirez.irouter;

import java.io.IOException;

public interface Call<R> {
    Request request();

    Response execute() throws IOException;

//    void enqueue(Callback responseCallback);

//    void cancel();


//    boolean isExecuted();

//    boolean isCanceled();

    Call<R> clone();

    interface Factory {
        <R> Call<R> newCall(ServiceMethod method,  Object[] args);

        <R>  Call<R> newCall(Request request);
    }
}
