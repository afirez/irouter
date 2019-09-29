package com.afirez.irouter;

import io.reactivex.Observable;

public class Response {

    private Request request;
    private Observable<Result> rxResult;

    public static Response create(Request request, Observable<Result> rxResult) {
        return new Response(request, rxResult);
    }

    private Response(Request request, Observable<Result> rxResult) {
        this.request = request;
        this.rxResult = rxResult;
    }

    public Request request() {
        return request;
    }


    public Observable<Result> rxResult() {
        return rxResult;
    }
}
