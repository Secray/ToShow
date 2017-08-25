package com.secray.toshow.mvp.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;

import com.secray.toshow.Constant;
import com.secray.toshow.Utils.Log;
import com.secray.toshow.mvp.contract.MainContract;
import com.tbruyelle.rxpermissions.RxPermissions;

import javax.inject.Inject;

import rx.Observable;

import static com.secray.toshow.MainActivity.REQUEST_CODE;

/**
 * Created by xiekui on 17-8-22.
 */

public class MainPresenter implements MainContract.Presenter {
    MainContract.View mView;

    @Inject
    public MainPresenter() {

    }

    @Override
    public void checkPermissions() {
        RxPermissions rxPermissions = new RxPermissions(mView.getActivityInstance());
        Observable.just(null)
                .compose(rxPermissions.ensureEach(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .subscribe(permission -> {
                    if (permission.granted) {
                        mView.startPickPhotoActivity();
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        mView.showPermissionRequestDialog();
                    } else {
                        mView.showPermissionDeniedMessage();
                    }
                }, throwable -> Log.e(throwable.getMessage()), () -> Log.i("Completed"));
    }

    @Override
    public void bindView(MainContract.View view) {
        mView = view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (requestCode == REQUEST_CODE) {
            if (results.length > 0
                    && results[0] == PackageManager.PERMISSION_GRANTED) {
                mView.startPickPhotoActivity();
            }
        }
    }
}
