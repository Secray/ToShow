package com.secray.toshow.mvp.presenter;

import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;

import com.secray.toshow.Utils.Constant;
import com.secray.toshow.Utils.RxHelper;
import com.secray.toshow.mvp.contract.CropContract;
import com.secray.toshow.mvp.contract.MosaicContract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import cn.jarlen.photoedit.operate.OperateUtils;
import rx.Observable;

/**
 * Created by user on 2017/10/11 0011.
 */

public class CropPresenter implements CropContract.Presenter {
    private CropContract.View mView;
    private String mPath;
    private Bitmap mLastBitmap;

    @Inject
    public CropPresenter() {

    }

    @Override
    public void bindView(CropContract.View view) {
        mView = view;
    }

    @Override
    public void savePic(Bitmap bitmap) {
        Observable.create(
                (Observable.OnSubscribe<Bitmap>) subscriber
                        -> subscriber.onNext(bitmap))
                .delay(1000, TimeUnit.MILLISECONDS)
                .compose(RxHelper.applyIoSchedulers())
                .map(pic -> saveBitmap(pic, "toshow" + System.currentTimeMillis() + ""))
                .subscribe(b -> mView.showMessage(b));
    }

    @Override
    public void generateBitmap(String path, View view, OperateUtils operateUtils) {
        mPath = path;
        Observable.create(
                (Observable.OnSubscribe<Bitmap>) subscriber ->
                        subscriber.onNext(operateUtils.compressionFiller(path, view)))
                .compose(RxHelper.applyIoSchedulers())
                .subscribe(bitmap -> mView.showImage(bitmap));
    }

    @Override
    public void loadLastBitmap() {
        if (mLastBitmap != null && !mLastBitmap.isRecycled()) {
            mView.showImage(mLastBitmap);
        }
    }

    @Override
    public String getPath() {
        return mPath;
    }

    private boolean saveBitmap(Bitmap bitmap, String name) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) && getSDFreeSize() >= 100) {
            File dir = new File(Constant.PIC_SAVE_PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(Constant.PIC_SAVE_PATH + name + ".jpg");
            FileOutputStream out;

            try {
                out = new FileOutputStream(file);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                    out.flush();
                    out.close();
                }
                mLastBitmap = bitmap;
                mPath = file.getPath();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public long getSDFreeSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        long blockSize = sf.getBlockSizeLong();
        long freeBlocks = sf.getAvailableBlocksLong();
        return (freeBlocks * blockSize) / 1024 / 1024;
    }
}
