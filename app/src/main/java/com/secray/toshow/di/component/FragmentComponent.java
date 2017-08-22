package com.secray.toshow.di.component;

import com.secray.toshow.di.FragmentScope;
import com.secray.toshow.di.module.FragmentModule;

import dagger.Component;

/**
 * Created by xiekui on 17-8-15.
 */

@Component(modules = FragmentModule.class, dependencies = ApplicationComponent.class)
@FragmentScope
public interface FragmentComponent {

}
