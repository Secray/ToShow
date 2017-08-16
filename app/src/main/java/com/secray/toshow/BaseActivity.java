package com.secray.toshow;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by xiekui on 17-8-16.
 */

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        ButterKnife.bind(this);

        initViews();
        onWork();
    }

    protected abstract @LayoutRes int getLayoutResId();
    protected abstract void initViews();
    protected abstract void onWork();
}
