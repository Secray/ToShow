package com.secray.toshow.mvp.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

import com.secray.toshow.Constant;
import com.secray.toshow.Utils.Log;
import com.secray.toshow.mvp.contract.MainContract;
import com.tbruyelle.rxpermissions.RxPermissions;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by xiekui on 17-8-22.
 */

public class MainPresenter implements MainContract.Presenter {
    MainContract.View mView;

    @Inject
    public MainPresenter() {

    }

    @Override
    public void checkPermissions(Activity activity) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        Observable.just(null)
                .compose(rxPermissions.ensureEach(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .subscribe(permission -> {
                    if (permission.granted) {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        activity.startActivityForResult(intent, Constant.REQUSET_PICK_PHOTO_CODE);
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
}
