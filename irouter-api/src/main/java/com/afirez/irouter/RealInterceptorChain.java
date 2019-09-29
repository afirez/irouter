package com.afirez.irouter;

import java.io.IOException;
import java.util.List;

final class RealInterceptorChain implements Interceptor.Chain{
    private final List<Interceptor> interceptors;
    private final int index;
    private final Request request;
    private final Call call;
    private int calls = 0;

    public RealInterceptorChain(List<Interceptor> interceptors, int index, Request request, Call call) {
        this.interceptors = interceptors;
        this.index = index;
        this.request = request;
        this.call = call;
    }

    @Override
    public Request request() {
        return request;
    }

    @Override
    public Response proceed(Request request) throws IOException {
        if (index >= interceptors.size()) throw new AssertionError();

        calls++;

        if (calls > 1) {
            throw new IllegalStateException("IRouter interceptor " + interceptors.get(index - 1)
                    + " must call proceed() exactly once");
        }

        RealInterceptorChain next = new RealInterceptorChain(interceptors, index + 1, request, call);
        Interceptor interceptor = interceptors.get(index);
        Response response = interceptor.intercept(next);

//        if (index + 1 < interceptors.size() && next.calls != 1) {
//            throw new IllegalStateException("IRouter interceptor " + interceptor
//                    + " must call proceed() exactly once");
//        }

        if (response == null) {
            throw new NullPointerException("interceptor " + interceptor + " returned null");
        }

        return response;
    }


    @Override
    public Call call() {
        return call;
    }

}
