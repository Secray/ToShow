package com.secray.toshow.activity;

import android.animation.AnimatorSet;
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
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.secray.toshow.R;
import com.secray.toshow.Utils.Constant;
import com.secray.toshow.di.component.ApplicationComponent;
import com.secray.toshow.di.component.DaggerActivityComponent;
import com.secray.toshow.di.module.ActivityModule;
import com.secray.toshow.mvp.contract.TuneImageContract;
import com.secray.toshow.mvp.presenter.TuneImagePresenter;
import com.secray.toshow.widget.QMUITipDialog;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jarlen.photoedit.enhance.PhotoEnhance;
import cn.jarlen.photoedit.operate.OperateUtils;

/**
 * Created by user on 2017/10/11 0011.
 */

public class TuneImageActivity extends BaseActivity implements TuneImageContract.View, SeekBar.OnSeekBarChangeListener {
    @BindView(R.id.top_bar)
    Toolbar mToolbar;
    @BindView(R.id.tune_bottom)
    LinearLayout mBottomAction;
    @BindView(R.id.seek_saturation)
    SeekBar mSeekSaturation;
    @BindView(R.id.seek_brightness)
    SeekBar mSeekBrightness;
    @BindView(R.id.seek_contrast)
    SeekBar mSeekContrast;
    @BindView(R.id.action_open)
    TextView mBackAction;
    @BindView(R.id.photo)
    ImageView mPicture;

    @Inject
    TuneImagePresenter mPresenter;

    QMUITipDialog mTipDialog;
    OperateUtils mOperateUtils;
    boolean mIsFirst = true;
    boolean mIsChanged;
    boolean mIsShow = true;
    Bitmap mBitmap;

    PhotoEnhance mPe;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_tune;
    }

    @Override
    protected void initViews() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
        }
        mBackAction.setText(R.string.back);
        mSeekBrightness.setOnSeekBarChangeListener(this);
        mSeekSaturation.setOnSeekBarChangeListener(this);
        mSeekContrast.setOnSeekBarChangeListener(this);

        mSeekBrightness.setMax(255);
        mSeekBrightness.setProgress(128);

        mSeekSaturation.setMax(255);
        mSeekSaturation.setProgress(128);

        mSeekContrast.setMax(255);
        mSeekContrast.setProgress(128);
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
        mPe = new PhotoEnhance(bitmap);
        mBitmap = bitmap;
        mPicture.setImageBitmap(bitmap);
        if (mIsFirst) {
            ObjectAnimator alpha = ObjectAnimator.ofFloat(mPicture, "alpha", 0f, 1f);
            alpha.start();
            mIsFirst = false;
        }
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
    protected void onDestroy() {
        super.onDestroy();
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mPe != null) {
            int type = 0;
            switch (seekBar.getId()) {
                case R.id.seek_brightness:
                    mPe.setBrightness(progress);
                    type = mPe.Enhance_Brightness;
                    break;
                case R.id.seek_contrast:
                    mPe.setContrast(progress);
                    type = mPe.Enhance_Contrast;
                    break;
                case R.id.seek_saturation:
                    mPe.setSaturation(progress);
                    type = mPe.Enhance_Saturation;
                    break;
            }
            if (progress != 128) {
                mIsChanged = true;
            } else {
                mIsChanged = false;
            }

            mBitmap = mPe.handleImage(type);
            mPicture.setImageBitmap(mBitmap);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

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

    @OnClick({R.id.photo, R.id.action_open})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.photo:
                if (mIsShow) {
                    hideBottomAction();
                    hideToolbar();
                    mIsShow = false;
                } else {
                    showBottomAction();
                    showToolbar();
                    mIsShow = true;
                }
                break;
            case R.id.action_open:
                onBackPressed();
                break;
        }
    }

    private void back() {
        Intent i = new Intent(this, EditorActivity.class);
        i.putExtra("lastBitmap", mPresenter.getPath());
        setResult(Constant.REQUEST_EDIT_PHOTO_CODE, i);
        finish();
    }

    void showToolbar() {
        mToolbar.setVisibility(View.VISIBLE);
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mToolbar, "alpha", 0f, 1f);
        ObjectAnimator translateY = ObjectAnimator.ofFloat(mToolbar, "translationY", mToolbar.getY() + 10, 0);
        set.play(alpha).with(translateY);
        set.start();
    }

    void hideToolbar() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mToolbar, "alpha", 1f, 0f);
        ObjectAnimator translateY = ObjectAnimator.ofFloat(mToolbar, "translationY", 0, -mToolbar.getY() - 10);
        set.play(alpha).with(translateY);
        set.start();
    }

    void hideBottomAction() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mBottomAction, "alpha", 1f, 0f);
        ObjectAnimator translateY = ObjectAnimator.ofFloat(mBottomAction, "translationY", 0, mBottomAction.getY() + 10);
        set.play(alpha).with(translateY);
        set.start();
    }

    void showBottomAction() {
        mToolbar.setVisibility(View.VISIBLE);
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(mBottomAction, "alpha", 0f, 1f);
        ObjectAnimator translateY = ObjectAnimator.ofFloat(mBottomAction, "translationY", mBottomAction.getY() + 10, 0);
        set.play(alpha).with(translateY);
        set.start();
    }
}
