package com.afirez.rxactivityresult;

import android.content.Intent;


class Request {
    private final Intent intent;
    private OnPreResult onPreResult;
    private OnResult onResult;

    public Request(Intent intent) {
        this.intent = intent;
    }

    void setOnPreResult(OnPreResult onPreResult) {
        this.onPreResult = onPreResult;
    }

    OnPreResult onPreResult() {
        return onPreResult;
    }

    public void setOnResult(OnResult onResult) {
        this.onResult = onResult;
    }

    public OnResult onResult() {
        return onResult;
    }

    public Intent intent() {
        return intent;
    }
}
