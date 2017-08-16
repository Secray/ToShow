package com.secray.toshow;

import android.app.Application;
import android.graphics.Typeface;

/**
 * Created by xiekui on 17-8-15.
 */

public class App extends Application {
    private static final String CANARO_EXTRA_BOLD_PATH = "fonts/canaro_extra_bold.otf";
    public static Typeface sCanaroExtraBold;

    @Override
    public void onCreate() {
        super.onCreate();
        initTypeface();
    }

    private void initTypeface() {
        sCanaroExtraBold = Typeface.createFromAsset(getAssets(), CANARO_EXTRA_BOLD_PATH);
    }
}
