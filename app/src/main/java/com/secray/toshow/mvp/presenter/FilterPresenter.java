package com.secray.toshow.mvp.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;

import com.secray.toshow.R;
import com.secray.toshow.Utils.BitmapUtils;
import com.secray.toshow.Utils.Constant;
import com.secray.toshow.Utils.Log;
import com.secray.toshow.Utils.RxHelper;
import com.secray.toshow.di.ContextScope;
import com.secray.toshow.mvp.contract.FilterContract;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import cn.jarlen.photoedit.filters.NativeFilter;
import cn.jarlen.photoedit.operate.OperateUtils;
import cn.jarlen.photoedit.utils.LittleUtil;
import rx.Observable;

import static com.secray.toshow.Utils.ImageFilterUtil.FILTER_BROWN;
import static com.secray.toshow.Utils.ImageFilterUtil.FILTER_CARICATURE;
import static com.secray.toshow.Utils.ImageFilterUtil.FILTER_GRAY;
import static com.secray.toshow.Utils.ImageFilterUtil.FILTER_LOMO;
import static com.secray.toshow.Utils.ImageFilterUtil.FILTER_NOSTALGIA;
import static com.secray.toshow.Utils.ImageFilterUtil.FILTER_PENCIL;

/**
 * Created by user on 2017/10/19 0019.
 */

public class FilterPresenter implements FilterContract.Presenter {
    private FilterContract.View mView;
    private Context mContext;
    private NativeFilter mNativeFilter;
    private String mPath;

    @Inject
    public FilterPresenter(@ContextScope Context context) {
        this.mContext = context;
        mNativeFilter = new NativeFilter();
    }

    @Override
    public void bindView(FilterContract.View view) {
        mView = view;
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
    public void generateIcon(String path) {
        Observable.create(
                (Observable.OnSubscribe<List<Bitmap>>) subscriber ->
                        subscriber.onNext(getIcons(path)))
                .compose(RxHelper.applyIoSchedulers())
                .subscribe(bitmap -> mView.showIcons(bitmap));
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
    public void filterBitmap(Bitmap bitmap, int type, float degree) {
        mView.showImage(getFilterBitmap(bitmap, type, degree));
    }

    @Override
    public String getPath() {
        return mPath;
    }

    private List<Bitmap> getIcons(String path) {
        ArrayList<Bitmap> icons = new ArrayList<>();
        Bitmap origin = BitmapUtils.createBitmapThumbnail(path,
                LittleUtil.dip2px(mContext, 60),
                LittleUtil.dip2px(mContext, 60));
        int w = origin.getWidth();
        int h = origin.getHeight();
        int []pix = new int[w * h];
        origin.getPixels(pix, 0, w, 0, 0, w, h);
        int[] type = mContext.getResources().getIntArray(R.array.filter_type);
        for (int i : type) {
            int []result;
            switch (i) {
                case FILTER_BROWN:
                    result = mNativeFilter.brown(pix, w, h, 1);
                    break;
                case FILTER_CARICATURE:
                    result = mNativeFilter.comics(pix, w, h, 1);
                    break;
                case FILTER_GRAY:
                    result = mNativeFilter.gray(pix, w, h, 1);
                    break;
                case FILTER_PENCIL:
                    result = mNativeFilter.sketchPencil(pix, w, h, 1);
                    break;
                case FILTER_NOSTALGIA:
                    result = mNativeFilter.nostalgic(pix, w, h, 1);
                    break;
                case FILTER_LOMO:
                    result = mNativeFilter.lomo(pix, w, h, 1);
                    break;
                default:
                    result = pix;
                    break;
            }
            icons.add(Bitmap.createBitmap(result, w, h,
                    Bitmap.Config.ARGB_8888));
        }
        origin.recycle();
        return icons;
    }

    private Bitmap getFilterBitmap(Bitmap bitmap, int type, float degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int []pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int []result;
        switch (type) {
            case FILTER_PENCIL:
                result = mNativeFilter.sketchPencil(pix, w, h, degree);
                break;
            case FILTER_BROWN:
                result = mNativeFilter.brown(pix, w, h, degree);
                break;
            case FILTER_CARICATURE:
                result = mNativeFilter.comics(pix, w, h, degree);
                break;
            case FILTER_LOMO:
                result = mNativeFilter.lomo(pix, w, h, degree);
                break;
            case FILTER_GRAY:
                result = mNativeFilter.gray(pix, w, h, degree);
                break;
            case FILTER_NOSTALGIA:
                result = mNativeFilter.nostalgic(pix, w, h, degree);
                break;
            default:
                result = pix;
                break;
        }
        return Bitmap.createBitmap(result, w, h,
                Bitmap.Config.ARGB_8888);
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
