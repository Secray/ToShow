package com.secray.toshow;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import com.secray.toshow.Utils.Log;
import com.secray.toshow.di.component.ApplicationComponent;
import com.secray.toshow.di.component.DaggerApplicationComponent;
import com.secray.toshow.di.module.ApplicationModule;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by xiekui on 17-8-15.
 */

public class App extends Application {
    private static final String CANARO_EXTRA_BOLD_PATH = "fonts/canaro_extra_bold.otf";
    public static Typeface sCanaroExtraBold;
    private static String[] mEsFontType;
    private static String[] mCnFontType;
    private ArrayList<Typeface> mTypefaces;
    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initTypeface();
        initDI();
        mTypefaces = new ArrayList<>();
        initTypefaces();
    }

    private void initDI() {
        mApplicationComponent = DaggerApplicationComponent.builder().
                applicationModule(new ApplicationModule(this)).build();
    }

    private void initTypefaces() {
        try {
            mEsFontType = getAssets().list("fonts/es");
            Log.d("size = " + mEsFontType.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String s : mEsFontType) {
            Log.d("path = " + s);
            mTypefaces.add(Typeface.createFromAsset(getAssets(), "fonts/es/" + s));
        }
    }

    public ArrayList<Typeface> getTypefaces() {
        return mTypefaces;
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
