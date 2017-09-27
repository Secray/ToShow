package com.secray.toshow.Utils;

import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by user on 2017/9/25 0025.
 */

public class ViewUtils {

    public static void snackBarAddView(Snackbar snackBar, View view, int index) {
        View snackBarView = snackBar.getView();
        Snackbar.SnackbarLayout snackBarLayout = (Snackbar.SnackbarLayout) snackBarView;
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.gravity = Gravity.CENTER_VERTICAL;
        snackBarLayout.addView(view, index, p);
    }

}
