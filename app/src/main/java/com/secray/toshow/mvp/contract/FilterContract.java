package com.secray.toshow.mvp.contract;

import android.graphics.Bitmap;

import com.secray.toshow.mvp.BasePresenter;
import com.secray.toshow.mvp.BaseView;

import java.util.List;

import cn.jarlen.photoedit.operate.OperateUtils;

/**
 * Created by user on 2017/10/19 0019.
 */

public interface FilterContract {
    interface View extends BaseView<Presenter> {
        void showImage(Bitmap bitmap);
        void showMessage(boolean success);
        void showIcons(List<Bitmap> icons);
    }

    interface Presenter extends BasePresenter<View> {
        void bindView(View view);
        void generateBitmap(String path, android.view.View view, OperateUtils operateUtils);
        void generateIcon(String path);
        void savePic(Bitmap bitmap);
        void filterBitmap(Bitmap bitmap, int type, float degree);
        String getPath();
    }
}
