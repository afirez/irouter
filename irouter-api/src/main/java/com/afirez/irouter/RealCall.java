package com.afirez.irouter;

import java.io.IOException;
import java.util.List;

import static com.afirez.irouter.Utils.throwIfFatal;

final class RealCall<R> implements Call<R> {

    private IRouter IRouter;
    private Request originalRequest;
    private ServiceMethod<?, ?> serviceMethod;
    private Object[] args;

    private Throwable creationFailure;
    private volatile boolean canceled;
    private boolean executed;

    public RealCall(IRouter IRouter, ServiceMethod<?, ?> serviceMethod, Object[] args) {
        this.IRouter = IRouter;
        this.serviceMethod = serviceMethod;
        this.args = args;
    }

    public RealCall(IRouter IRouter, Request request) {
        this.IRouter = IRouter;
        this.originalRequest = request;
    }

    @Override
    public Request request() {
        if (originalRequest != null) {
            return originalRequest;
        }
        try {
            return (originalRequest = requestFromServiceMethod());
        } catch (IOException e) {
            creationFailure = e;
            throw new RuntimeException("Unable to create request.", creationFailure);
        } catch (Throwable t) {
            throwIfFatal(t);
            creationFailure = t;
            throw t;
        }
    }

    private Request requestFromServiceMethod() throws IOException {
        return serviceMethod.toRequest(args);
    }

    @Override
    public Response execute() throws IOException {
        Request request;
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already executed.");
            executed = true;

            if (creationFailure != null) {
                if (creationFailure instanceof IOException) {
                    throw (IOException) creationFailure;
                } else if (creationFailure instanceof RuntimeException) {
                    throw (RuntimeException) creationFailure;
                } else {
                    throw (Error) creationFailure;
                }
            }

            try {
                request = request();
            } catch (Throwable e) {
                throwIfFatal(e);
                creationFailure = e;
                throw e;
            }
        }

        originalRequest = request;

        Response response = getResponseWithInterceptorChain();

        if (canceled) {
            throw new IOException("canceled");
        }
        return parseResponse(response);
    }

    private Response parseResponse(Response rawResponse) {
        return rawResponse;
    }

//    @Override
//    public boolean isExecuted() {
//        return executed;
//    }

    @Override
    public RealCall clone() {
        if (serviceMethod == null) {
            return new RealCall(IRouter, originalRequest);
        }
        return new RealCall(IRouter, serviceMethod, args);
    }

    Response getResponseWithInterceptorChain() throws IOException {
        List<Interceptor> interceptors = IRouter.interceptors();
        Interceptor.Chain chain = new RealInterceptorChain(interceptors, 0, null, null);
        return chain.proceed(originalRequest);
    }


}
