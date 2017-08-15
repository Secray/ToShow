package com.secray.toshow.dagger.component;

import android.app.Activity;
import android.content.Context;

import com.secray.toshow.dagger.ActivityScope;
import com.secray.toshow.dagger.ContextScope;
import com.secray.toshow.dagger.module.ActivityModule;

import dagger.Component;

/**
 * Created by xiekui on 17-8-15.
 */

@ActivityScope
@Component(modules = ActivityModule.class, dependencies = ApplicationComponent.class)
public interface ActivityComponent {
    Activity activity();
    void inject(Activity activity);

    @ContextScope("Activity")
    Context getActivityContext();
}
