package com.afirez.rxactivityresult;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

public class HolderFragment extends Fragment {

    public static final String TAG = "HolderFragment";

    private Subject<AResult> rxUiResult = BehaviorSubject.<AResult>create().toSerialized();

    public Observable<AResult> rxActivityResult() {
        return rxUiResult;
    }

    public static HolderFragment with(FragmentManager fm) {
        HolderFragment rf = (HolderFragment) fm.findFragmentByTag(HolderFragment.TAG);
        if (rf == null) {
            rf = new HolderFragment();
            fm.beginTransaction().add(rf, HolderFragment.TAG).commit();
        }
        return rf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        rxUiResult.onNext(new AResult(requestCode, resultCode, data));
    }

    public static int startActivityForResult(
            Object host,
            Intent intent,
            int requestCode) {
        FragmentManager fm;
        if (host instanceof Activity) {
            fm = ((Activity) host).getFragmentManager();
        } else if (host instanceof Fragment) {
            Activity activity = ((Fragment) host).getActivity();
            if (activity == null) {
                throw new IllegalStateException("Illegal State: activity == null");
            }
            fm = activity.getFragmentManager();
        } else {
            throw new IllegalArgumentException("Illegal Argument: host");
        }

        if (fm != null) {
            return startActivityForResultReal(fm, intent, requestCode);
        }
        throw new IllegalStateException("Illegal State: fm == null");
    }

    public static int startActivityForResultReal(
            FragmentManager fm,
            Intent intent,
            int requestCode) {
        HolderFragment resultFragment = with(fm);
        resultFragment.startActivityForResult(intent, requestCode);
        return requestCode;
    }
}