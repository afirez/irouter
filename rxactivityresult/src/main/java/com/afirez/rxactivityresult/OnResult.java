package com.afirez.rxactivityresult;

import android.content.Intent;

import java.io.Serializable;

interface OnResult extends Serializable {
    void onResult(int requestCode, int resultCode, Intent data);
    void onError(Throwable throwable);
}
