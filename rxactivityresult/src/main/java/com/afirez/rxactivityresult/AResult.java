package com.afirez.rxactivityresult;

import android.content.Intent;

public class AResult {
    private final int resultCode;
    private final int requestCode;
    private final Intent data;

    public AResult(int requestCode, int resultCode, Intent data) {
        this.resultCode = resultCode;
        this.requestCode = requestCode;
        this.data = data;
    }

    public int requestCode() {
        return requestCode;
    }

    public int resultCode() {
        return resultCode;
    }

    public Intent data() {
        return data;
    }
}
