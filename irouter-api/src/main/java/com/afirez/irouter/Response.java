package com.afirez.irouter;

import android.app.Activity;
import com.afirez.irouter.exception.RouteCanceledException;
import io.reactivex.Observable;

public class Response {

    private Request request;
    private int resultCode = Activity.RESULT_CANCELED;
    private Object data;

    public static Response canceled(Request request, int resultCode, String msg) {
        Observable<Result> data = Observable.error(new RouteCanceledException(msg));
        return new Response(request, resultCode, data);
    }

    public static Response create(Request request, int resultCode, Object data) {
        return new Response(request, resultCode, data);
    }

    private Response(Request request, int resultCode, Object data) {
        this.request = request;
        this.resultCode = resultCode;
        this.data = data;
    }

    public Request request() {
        return request;
    }

    public int resultCode() {
        return resultCode;
    }


    public Object data() {
        return data;
    }
}
