package com.secray.toshow.di.component;

import android.app.Activity;
import android.content.Context;

import com.secray.toshow.MainActivity;
import com.secray.toshow.di.ActivityScope;
import com.secray.toshow.di.ContextScope;
import com.secray.toshow.di.module.ActivityModule;

import dagger.Component;

/**
 * Created by xiekui on 17-8-15.
 */

@ActivityScope
@Component(modules = ActivityModule.class, dependencies = ApplicationComponent.class)
public interface ActivityComponent {
    Activity activity();

    void inject(Activity activity);
    void inject(MainActivity activity);

    @ContextScope("Activity")
    Context getActivityContext();
}
