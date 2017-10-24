package com.secray.toshow.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * Created by user on 2017/10/11 0011.
 */

public class BitmapUtils {
    public static Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null || origin.isRecycled()) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Log.i("ratio = " + ratio + " w = " + width);
        Matrix matrix = new Matrix();
        matrix.postScale(ratio, ratio);
        Bitmap bitmap = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, true);
        if (bitmap.equals(origin)) {
            return bitmap;
        }
        return bitmap;
    }

    public static Bitmap createBitmapThumbnail(Bitmap bitMap, int newWidth, int newHeight) {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitMap, 0, 0, width, height,
                matrix, true);
    }

    public static Bitmap createBitmapThumbnail(String path, int newWidth, int newHeight) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, opt);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
    }
}
