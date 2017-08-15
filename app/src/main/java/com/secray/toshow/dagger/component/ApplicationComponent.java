package com.secray.toshow.dagger.component;

import android.content.Context;

import com.secray.toshow.App;
import com.secray.toshow.dagger.ContextScope;
import com.secray.toshow.dagger.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by xiekui on 17-8-15.
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    App app();

    @ContextScope
    Context getApplicationContext();
}
