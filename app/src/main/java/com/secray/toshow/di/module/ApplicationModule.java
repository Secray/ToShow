package com.secray.toshow.di.module;

import android.content.Context;

import com.secray.toshow.App;
import com.secray.toshow.di.ContextScope;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by xiekui on 17-8-15.
 */

@Module
public class ApplicationModule {
    private final App mApp;

    public ApplicationModule(App app) {
        this.mApp = app;
    }

    @Singleton
    @Provides
    App provideApp() {
        return mApp;
    }

    @Provides
    @ContextScope
    @Singleton
    Context provideApplicationContext() {
        return mApp;
    }
}
