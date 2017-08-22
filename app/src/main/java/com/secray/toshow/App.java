package com.secray.toshow;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import com.secray.toshow.di.component.ApplicationComponent;
import com.secray.toshow.di.component.DaggerApplicationComponent;
import com.secray.toshow.di.module.ApplicationModule;

/**
 * Created by xiekui on 17-8-15.
 */

public class App extends Application {
    private static final String CANARO_EXTRA_BOLD_PATH = "fonts/canaro_extra_bold.otf";
    public static Typeface sCanaroExtraBold;
    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initTypeface();
        initDI();
    }

    private void initDI() {
        mApplicationComponent = DaggerApplicationComponent.builder().
                applicationModule(new ApplicationModule(this)).build();
    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }

    private void initTypeface() {
        sCanaroExtraBold = Typeface.createFromAsset(getAssets(), CANARO_EXTRA_BOLD_PATH);
    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }
}
