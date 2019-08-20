package com.afirez.rxactivityresult;

import android.content.Intent;
import io.reactivex.Observable;

import java.io.Serializable;

interface OnPreResult extends Serializable {
    Observable<AResult> onPreResult(int requestCode, int resultCode, Intent data);
}
