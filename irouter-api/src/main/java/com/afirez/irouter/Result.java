package com.afirez.irouter;

public class Result {

    private final int call;
    private final int requestCode;
    private final int resultCode;
    private final Object data;

    public Result(int call, int requestCode, int resultCode, Object data) {
        this.call = call;
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }

    public int getCall() {
        return call;
    }

    public int requestCode() {
        return requestCode;
    }

    public int resultCode() {
        return resultCode;
    }

    public Object data() {
        return data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "call=" + call +
                ", requestCode=" + requestCode +
                ", resultCode=" + resultCode +
                ", data=" + data +
                '}';
    }
}
