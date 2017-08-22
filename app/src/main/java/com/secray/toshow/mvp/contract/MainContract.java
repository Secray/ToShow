package com.secray.toshow.mvp.contract;

import android.app.Activity;

import com.secray.toshow.mvp.BasePresenter;
import com.secray.toshow.mvp.BaseView;

/**
 * Created by xiekui on 17-8-22.
 */

public interface MainContract {
    interface View extends BaseView<Presenter> {
        void showPermissionRequestDialog();
        void showPermissionDeniedMessage();
    }

    interface Presenter extends BasePresenter<View> {
        void checkPermissions(Activity activity);
        void bindView(View view);
    }
}
