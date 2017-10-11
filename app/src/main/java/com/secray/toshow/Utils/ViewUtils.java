package com.secray.toshow.Utils;

import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.secray.toshow.activity.AddTextActivity;
import com.secray.toshow.activity.CropActivity;
import com.secray.toshow.activity.FilterActivity;
import com.secray.toshow.activity.FramesActivity;
import com.secray.toshow.activity.MosaicActivity;
import com.secray.toshow.activity.RotateActivity;
import com.secray.toshow.activity.ScrawlActivity;
import com.secray.toshow.activity.TuneImageActivity;
import com.secray.toshow.activity.WatermarkActivity;

/**
 * Created by user on 2017/9/25 0025.
 */

public class ViewUtils {
    public static final Class[] OPERATION_ACTIVITYS =
            new Class[]{TuneImageActivity.class, CropActivity.class, FramesActivity.class,
                    MosaicActivity.class, ScrawlActivity.class, FilterActivity.class,
                    RotateActivity.class, WatermarkActivity.class, AddTextActivity.class};
    public static void snackBarAddView(Snackbar snackBar, View view, int index) {
        View snackBarView = snackBar.getView();
        Snackbar.SnackbarLayout snackBarLayout = (Snackbar.SnackbarLayout) snackBarView;
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.gravity = Gravity.CENTER_VERTICAL;
        snackBarLayout.addView(view, index, p);
    }

}
