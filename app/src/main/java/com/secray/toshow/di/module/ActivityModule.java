package com.secray.toshow.di.module;

import android.app.Activity;
import android.content.Context;

import com.secray.toshow.di.ActivityScope;
import com.secray.toshow.di.ContextScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by xiekui on 17-8-15.
 */

@Module
public class ActivityModule {
    private final Activity mActivity;

    public ActivityModule(Activity activity) {
        this.mActivity = activity;
    }

    @Provides
    @ActivityScope
    Activity provideActivity() {
        return mActivity;
    }

    @Provides
    @ActivityScope
    @ContextScope("Activity")
    Context provideActivityContext() {
        return mActivity;
    }
}
