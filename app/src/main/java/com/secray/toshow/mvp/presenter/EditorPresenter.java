package com.secray.toshow.mvp.presenter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

import com.secray.toshow.Utils.RxHelper;
import com.secray.toshow.di.ContextScope;
import com.secray.toshow.mvp.contract.EditorContract;

import javax.inject.Inject;

import cn.jarlen.photoedit.operate.OperateUtils;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by user on 2017/10/10 0010.
 */

public class EditorPresenter implements EditorContract.Presenter {
    private EditorContract.View mView;
    private Context mContext;
    private String mPath;

    @Inject
    public EditorPresenter(@ContextScope Context context) {
        mContext = context;
    }

    @Override
    public void bindView(EditorContract.View view) {
        mView = view;
    }

    @Override
    public void generateBitmap(Uri uri, View view, OperateUtils operateUtils) {
        Observable.create(
                (Observable.OnSubscribe<Bitmap>) subscriber -> {
                    if (uri == null || "".equals(uri.toString())) {
                        return;
                    }
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = mContext.getContentResolver().query(uri,
                            filePathColumn, null, null, null);
                    if (cursor == null) return;
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    mPath = cursor.getString(columnIndex);
                    cursor.close();
                    subscriber.onNext(operateUtils.compressionFiller(mPath, view));
                })
                .compose(RxHelper.applyIoSchedulers())
                .subscribe(bitmap -> mView.showImage(bitmap));
    }

    @Override
    public void loadLastBitmap(String path, View view, OperateUtils operateUtils) {
        Observable.create(
                (Observable.OnSubscribe<Bitmap>) subscriber -> {
                    subscriber.onNext(operateUtils.compressionFiller(mPath, view));
                })
                .compose(RxHelper.applyIoSchedulers())
                .subscribe(bitmap -> mView.showImage(bitmap));
    }

    @Override
    public String getPath() {
        return mPath;
    }
}
