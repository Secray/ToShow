package com.secray.toshow.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.secray.toshow.R;
import com.secray.toshow.Utils.Constant;
import com.secray.toshow.di.component.ApplicationComponent;
import com.secray.toshow.di.component.DaggerActivityComponent;
import com.secray.toshow.di.module.ActivityModule;
import com.secray.toshow.mvp.contract.FramesContract;
import com.secray.toshow.mvp.presenter.FramesPresenter;
import com.secray.toshow.widget.QMUITipDialog;

import javax.inject.Inject;

import butterknife.BindView;
import cn.jarlen.photoedit.operate.OperateUtils;
import cn.jarlen.photoedit.photoframe.PhotoFrame;

/**
 * Created by user on 2017/10/11 0011.
 */

public class FramesActivity extends BaseActivity
        implements FramesContract.View, View.OnClickListener {
    @BindView(R.id.common_top_bar)
    Toolbar mToolbar;
    @BindView(R.id.picture)
    ImageView mPicture;
    @BindView(R.id.action_open)
    TextView mBackAction;

    QMUITipDialog mTipDialog;
    OperateUtils mOperateUtils;
    PhotoFrame mPhotoFrame;
    boolean mIsFirst = true;
    boolean mIsChanged;
    Bitmap mBitmap;

    @Inject
    FramesPresenter mPresenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_frames;
    }

    @Override
    protected void initViews() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
        }

        findViewById(R.id.photoRes_one).setOnClickListener(this);
        findViewById(R.id.photoRes_two).setOnClickListener(this);
        findViewById(R.id.photoRes_three).setOnClickListener(this);
        findViewById(R.id.photoRes_four).setOnClickListener(this);
        findViewById(R.id.photoRes_five).setOnClickListener(this);
        mBackAction.setOnClickListener(this);
        mBackAction.setText(R.string.back);
    }

    @Override
    protected void onWork() {
        mPresenter.bindView(this);
        mOperateUtils = new OperateUtils(this);
        String path = getIntent().getStringExtra("path");
        mPicture.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mPicture.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        mPresenter.generateBitmap(path, mPicture, mOperateUtils);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_clear);
        menuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:
                if (mIsChanged) {
                    mTipDialog = new QMUITipDialog.Builder(this)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                            .setTipWord(getString(R.string.progress_dialog_saving))
                            .create();
                    mTipDialog.show();
                    if (mBitmap != null && !mBitmap.isRecycled())
                    mPresenter.savePic(mBitmap);
                } else {
                    mTipDialog = new QMUITipDialog.Builder(this)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                            .setTipWord(getString(R.string.pic_not_changed_message))
                            .create();
                    mTipDialog.show();
                    mPicture.postDelayed(() -> mTipDialog.dismiss(), 1000);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void setupActivityComponent(ApplicationComponent applicationComponent) {
        DaggerActivityComponent
                .builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(applicationComponent)
                .build()
                .inject(this);
    }

    @Override
    public void showImage(Bitmap bitmap) {
        mPhotoFrame = new PhotoFrame(this, bitmap);
        mPicture.setImageBitmap(bitmap);
        if (mIsFirst) {
            ObjectAnimator alpha = ObjectAnimator.ofFloat(mPicture, "alpha", 0f, 1f);
            alpha.start();
            mIsFirst = false;
        }
    }

    @Override
    public void onBackPressed() {
        if (mIsChanged) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.exit_message)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> back())
                    .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        } else {
            back();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
    }

    private void back() {
        Intent i = new Intent(this, EditorActivity.class);
        i.putExtra("lastBitmap", mPresenter.getPath());
        setResult(Constant.REQUEST_EDIT_PHOTO_CODE, i);
        finish();
    }

    @Override
    public void showMessage(boolean success) {
        mTipDialog.dismiss();
        QMUITipDialog dialog = new QMUITipDialog.Builder(this)
                .setIconType(success ? QMUITipDialog.Builder.ICON_TYPE_SUCCESS
                        : QMUITipDialog.Builder.ICON_TYPE_FAIL)
                .setTipWord(success ? getString(R.string.save_success) : getString(R.string.save_failed))
                .create();
        dialog.show();
        mPicture.postDelayed(() -> {
            dialog.dismiss();
            back();
        }, 1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photoRes_one:
                mIsChanged = true;
                mPhotoFrame.setFrameType(PhotoFrame.FRAME_SMALL);
                mPhotoFrame.setFrameResources(
                        R.drawable.frame_around1_left_top,
                        R.drawable.frame_around1_left,
                        R.drawable.frame_around1_left_bottom,
                        R.drawable.frame_around1_bottom,
                        R.drawable.frame_around1_right_bottom,
                        R.drawable.frame_around1_right,
                        R.drawable.frame_around1_right_top,
                        R.drawable.frame_around1_top);
                mBitmap = mPhotoFrame.combineFrameRes();
                mPicture.setImageBitmap(mBitmap);
                break;
            case R.id.photoRes_two:
                mIsChanged = true;
                mPhotoFrame.setFrameType(PhotoFrame.FRAME_SMALL);
                mPhotoFrame.setFrameResources(
                        R.drawable.frame_around2_left_top,
                        R.drawable.frame_around2_left,
                        R.drawable.frame_around2_left_bottom,
                        R.drawable.frame_around2_bottom,
                        R.drawable.frame_around2_right_bottom,
                        R.drawable.frame_around2_right,
                        R.drawable.frame_around2_right_top,
                        R.drawable.frame_around2_top);
                mBitmap = mPhotoFrame.combineFrameRes();
                mPicture.setImageBitmap(mBitmap);
                break;
            case R.id.photoRes_three:
                mIsChanged = true;
                mPhotoFrame.setFrameType(PhotoFrame.FRAME_BIG);
                mPhotoFrame.setFrameResources(R.drawable.frame_big1);
                mBitmap = mPhotoFrame.combineFrameRes();
                mPicture.setImageBitmap(mBitmap);
                break;
            case R.id.photoRes_four:
                mIsChanged = true;
                mPhotoFrame.setFrameType(PhotoFrame.FRAME_BIG);
                mPhotoFrame.setFrameResources(R.drawable.frame_flower);
                mBitmap = mPhotoFrame.combineFrameRes();
                mPicture.setImageBitmap(mBitmap);
                break;
            case R.id.photoRes_five:
                mIsChanged = true;
                mPhotoFrame.setFrameType(PhotoFrame.FRAME_BIG);
                mPhotoFrame.setFrameResources(R.drawable.frame_gesture);
                mBitmap = mPhotoFrame.combineFrameRes();
                mPicture.setImageBitmap(mBitmap);
                break;
            case R.id.action_open:
                onBackPressed();
                break;
        }
    }
}
