package com.afirez.irouter;

public class Result {
    private final int requestCode;
    private final int resultCode;
    private final Object data;

    public Result(int requestCode, int resultCode, Object data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
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
                "requestCode=" + requestCode +
                ", resultCode=" + resultCode +
                ", data=" + data +
                '}';
    }
}
