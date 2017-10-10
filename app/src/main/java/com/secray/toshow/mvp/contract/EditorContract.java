package com.secray.toshow.mvp.contract;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

import com.secray.toshow.mvp.BasePresenter;
import com.secray.toshow.mvp.BaseView;

import cn.jarlen.photoedit.operate.OperateUtils;

/**
 * Created by xiekui on 2017/10/10 0010.
 */

public interface EditorContract {
    interface View extends BaseView<Presenter> {
        void showImage(Bitmap bitmap);
    }

    interface Presenter extends BasePresenter<View> {
        void bindView(View view);
        void generateBitmap(Uri uri, android.view.View view, OperateUtils operateUtils);
        void loadLastBitmap(String path, android.view.View view, OperateUtils operateUtils);
        String getPath();
    }
}
