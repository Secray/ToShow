package com.secray.toshow;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;

import com.secray.toshow.di.ActivityScope;
import com.secray.toshow.di.component.ApplicationComponent;
import com.secray.toshow.di.component.DaggerActivityComponent;
import com.secray.toshow.di.module.ActivityModule;
import com.secray.toshow.mvp.contract.MainContract;
import com.secray.toshow.mvp.presenter.MainPresenter;
import com.tbruyelle.rxpermissions.RxPermissions;


import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

@ActivityScope
public class MainActivity extends BaseActivity implements MainContract.View {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Inject
    MainPresenter mMainPresenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle(null);
        }
    }

    @Override
    protected void onWork() {
        RxPermissions rxPermissions = new RxPermissions(this);
        mMainPresenter.bindView(this);
    }

    @Override
    protected void setupActivityComponent(ApplicationComponent applicationComponent) {
        DaggerActivityComponent.builder().
                applicationComponent(applicationComponent).
                activityModule(new ActivityModule(this)).build().inject(this);
    }

    @OnClick(R.id.pick_photo)
    void onPickPhotoClick() {
        mMainPresenter.checkPermissions(this);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constant.REQUSET_PICK_PHOTO_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void showPermissionRequestDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.permission_tip)
                .setMessage(R.string.permission_message)
                .setNegativeButton(R.string.permission_go_on, (dialog, which) -> {
                    
                })
                .setPositiveButton(R.string.permission_cancel, (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    @Override
    public void showPermissionDeniedMessage() {
        new AlertDialog.Builder(this).setTitle(R.string.permission_denied_tip)
                .setMessage(R.string.permission_denied_message)
                .setNegativeButton(R.string.permission_go_to_setting, (dialog, which) -> {

                })
                .setPositiveButton(R.string.permission_cancel, (dialog, which) -> dialog.dismiss())
                .create().show();
    }
}
