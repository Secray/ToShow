package com.secray.toshow.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.secray.toshow.R;
import com.secray.toshow.Utils.Constant;
import com.secray.toshow.di.component.ApplicationComponent;
import com.secray.toshow.di.component.DaggerActivityComponent;
import com.secray.toshow.di.module.ActivityModule;
import com.secray.toshow.mvp.contract.CropContract;
import com.secray.toshow.mvp.presenter.CropPresenter;
import com.secray.toshow.widget.QMUITipDialog;

import javax.inject.Inject;

import butterknife.BindView;
import cn.jarlen.photoedit.crop.CropImageType;
import cn.jarlen.photoedit.crop.CropImageView;
import cn.jarlen.photoedit.operate.OperateUtils;

/**
 * Created by user on 2017/10/11 0011.
 */

public class CropActivity extends BaseActivity implements CropContract.View, View.OnClickListener {
    @BindView(R.id.crop_image)
    CropImageView mCropImageView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.action_open)
    TextView mBackAction;

    @Inject
    CropPresenter mPresenter;

    QMUITipDialog mTipDialog;
    OperateUtils mOperateUtils;
    boolean mIsFirst = true;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.op_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_clear);
        menuItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:
                if (mCropImageView.getCroppedImage() != null) {
                    mTipDialog = new QMUITipDialog.Builder(this)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                            .setTipWord(getString(R.string.progress_dialog_saving))
                            .create();
                    mTipDialog.show();
                    mPresenter.savePic(mCropImageView.getCroppedImage());
                } else {
                    mTipDialog = new QMUITipDialog.Builder(this)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                            .setTipWord(getString(R.string.pic_not_changed_message))
                            .create();
                    mTipDialog.show();
                    mCropImageView.postDelayed(() -> mTipDialog.dismiss(), 1000);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        Intent i = new Intent(this, EditorActivity.class);
        i.putExtra("lastBitmap", mPresenter.getPath());
        setResult(Constant.REQUEST_EDIT_PHOTO_CODE, i);
        finish();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_crop;
    }

    @Override
    protected void initViews() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
        }
        mBackAction.setText(R.string.back);
    }

    @Override
    protected void onWork() {
        mPresenter.bindView(this);
        mOperateUtils = new OperateUtils(this);
        String path = getIntent().getStringExtra("path");
        mCropImageView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mCropImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mPresenter.generateBitmap(path, mCropImageView, mOperateUtils);
            }
        });

    }

    @Override
    protected void setupActivityComponent(ApplicationComponent applicationComponent) {
        DaggerActivityComponent
                .builder()
                .applicationComponent(applicationComponent)
                .activityModule(new ActivityModule(this))
                .build()
                .inject(this);
    }

    @Override
    public void showImage(Bitmap bitmap) {
        Bitmap crop = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.crop_button);
        mCropImageView.setImageBitmap(bitmap);
        mCropImageView.setCropOverlayCornerBitmap(crop);
        mCropImageView.setGuidelines(CropImageType.CROPIMAGE_GRID_ON_TOUCH);
        mCropImageView.setFixedAspectRatio(false);
        if (mIsFirst) {
            ObjectAnimator alpha = ObjectAnimator.ofFloat(mCropImageView, "alpha", 0f, 1f);
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
        mCropImageView.postDelayed(() -> {
            dialog.dismiss();
            back();
        }, 1000);
        //mPresenter.loadLastBitmap();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_open:
                back();
                break;
        }
    }
}
