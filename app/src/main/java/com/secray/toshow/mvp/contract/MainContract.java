package com.secray.toshow.mvp.contract;

import android.app.Activity;
import android.content.Context;

import com.secray.toshow.mvp.BasePresenter;
import com.secray.toshow.mvp.BaseView;

/**
 * Created by xiekui on 17-8-22.
 */

public interface MainContract {
    interface View extends BaseView<Presenter> {
        void showPermissionRequestDialog();
        void showPermissionDeniedMessage();
        void startPickPhotoActivity();
        Activity getActivityInstance();
    }

    interface Presenter extends BasePresenter<View> {
        void checkPermissions();
        void bindView(View view);
        void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results);
    }
}
