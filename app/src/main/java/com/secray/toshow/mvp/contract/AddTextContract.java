package com.secray.toshow.mvp.contract;

import android.graphics.Bitmap;
import android.view.View;

import com.secray.toshow.mvp.BasePresenter;
import com.secray.toshow.mvp.BaseView;

import cn.jarlen.photoedit.operate.OperateUtils;

/**
 * Created by user on 2017/9/25 0025.
 */

public interface AddTextContract {
    interface View extends BaseView<Presenter> {
        void showImage(Bitmap bitmap);
        void showMessage(boolean success);
    }

    interface Presenter extends BasePresenter<View> {
        void bindView(View view);
        void generateBitmap(String path, android.view.View view, OperateUtils operateUtils);
        void savePic(android.view.View view);
        void loadLastBitmap();
        String getPath();
    }
}
