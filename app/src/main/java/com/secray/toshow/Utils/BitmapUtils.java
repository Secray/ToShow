package com.secray.toshow.Utils;

import android.graphics.Bitmap;
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
}
