package com.secray.toshow.dagger.component;

import com.secray.toshow.dagger.FragmentScope;
import com.secray.toshow.dagger.module.FragmentModule;

import dagger.Component;

/**
 * Created by xiekui on 17-8-15.
 */

@Component(modules = FragmentModule.class, dependencies = ApplicationComponent.class)
@FragmentScope
public interface FragmentComponent {

}
