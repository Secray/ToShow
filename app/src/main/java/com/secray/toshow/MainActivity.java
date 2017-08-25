package com.secray.toshow;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.nightonke.boommenu.BoomMenuButton;
import com.secray.toshow.di.ActivityScope;
import com.secray.toshow.di.component.ApplicationComponent;
import com.secray.toshow.di.component.DaggerActivityComponent;
import com.secray.toshow.di.module.ActivityModule;
import com.secray.toshow.mvp.contract.MainContract;
import com.secray.toshow.mvp.presenter.MainPresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

@ActivityScope
public class MainActivity extends BaseActivity implements MainContract.View {
    public static final int REQUEST_CODE = 0x01;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
   /* @BindView(R.id.main_img)
    ImageView mPicture;
    @BindView(R.id.bmb)
    BoomMenuButton mBmb;*/
    @BindView(R.id.main_stub)
    ViewStub mStub;
    @BindView(R.id.pick_photo)
    TextView mPickPhoto;

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
        mMainPresenter.checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mMainPresenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPickPhoto.setVisibility(View.GONE);
        mStub.inflate();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void showPermissionRequestDialog() {
        new AlertDialog.Builder(this).setTitle(R.string.permission_tip)
                .setMessage(R.string.permission_message)
                .setNegativeButton(R.string.permission_go_on, (dialog, which) ->
                        this.requestPermissions(
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE))
                .setPositiveButton(R.string.permission_cancel, (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    @Override
    public void showPermissionDeniedMessage() {
        new AlertDialog.Builder(this).setTitle(R.string.permission_denied_tip)
                .setMessage(R.string.permission_denied_message)
                .setNegativeButton(R.string.permission_go_to_setting, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                    startActivity(intent);
                })
                .setPositiveButton(R.string.permission_cancel, (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    @Override
    public void startPickPhotoActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constant.REQUSET_PICK_PHOTO_CODE);
    }

    @Override
    public Activity getActivityInstance() {
        return this;
    }
}
