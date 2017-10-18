package com.secray.toshow.di.component;

import android.app.Activity;
import android.content.Context;

import com.secray.toshow.activity.AddTextActivity;
import com.secray.toshow.activity.CropActivity;
import com.secray.toshow.activity.EditorActivity;
import com.secray.toshow.activity.FramesActivity;
import com.secray.toshow.activity.MainActivity;
import com.secray.toshow.activity.MosaicActivity;
import com.secray.toshow.activity.RotateActivity;
import com.secray.toshow.activity.ScrawlActivity;
import com.secray.toshow.activity.TuneImageActivity;
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
    void inject(EditorActivity activity);
    void inject(AddTextActivity activity);
    void inject(MosaicActivity activity);
    void inject(CropActivity activity);
    void inject(FramesActivity activity);
    void inject(RotateActivity activity);
    void inject(TuneImageActivity activity);
    void inject(ScrawlActivity activity);

    @ContextScope("Activity")
    Context getActivityContext();
}
