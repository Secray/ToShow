package com.secray.toshow.mvp.presenter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;

import com.secray.toshow.Utils.Constant;
import com.secray.toshow.Utils.Log;
import com.secray.toshow.Utils.RxHelper;
import com.secray.toshow.mvp.contract.AddTextContract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import cn.jarlen.photoedit.operate.OperateUtils;
import rx.Observable;

/**
 * Created by user on 2017/9/25 0025.
 */

public class AddTextPresenter implements AddTextContract.Presenter {
    private AddTextContract.View mView;

    @Inject
    public AddTextPresenter() {

    }


    @Override
    public void bindView(AddTextContract.View view) {
        mView = view;
    }

    @Override
    public void generateBitmap(String path, View view, OperateUtils operateUtils) {
        Observable.create(
                (Observable.OnSubscribe<Bitmap>) subscriber ->
                        subscriber.onNext(operateUtils.compressionFiller(path, view)))
                .compose(RxHelper.applyIoSchedulers())
                .subscribe(bitmap -> mView.showImage(bitmap));

    }

    @Override
    public void savePic(View view) {
        Observable.create(
                (Observable.OnSubscribe<Bitmap>) subscriber
                        -> subscriber.onNext(getBitmapByView(view)))
                .delay(1000, TimeUnit.MILLISECONDS)
                .compose(RxHelper.applyIoSchedulers())
                .map(bitmap -> saveBitmap(bitmap, "toshow" + System.currentTimeMillis() + ""))
                .subscribe(b -> mView.showMessage(b));
    }

    private Bitmap getBitmapByView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
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
