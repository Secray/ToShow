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
import com.secray.toshow.mvp.contract.RotateContract;
import com.secray.toshow.mvp.presenter.RotatePresenter;
import com.secray.toshow.widget.QMUITipDialog;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jarlen.photoedit.operate.OperateUtils;
import cn.jarlen.photoedit.utils.PhotoUtils;

/**
 * Created by user on 2017/10/11 0011.
 */

public class RotateActivity extends BaseActivity implements RotateContract.View {
    @BindView(R.id.top_bar)
    Toolbar mToolbar;
    @BindView(R.id.picture)
    ImageView mPicture;
    @BindView(R.id.action_open)
    TextView mBackAction;


    @Inject
    RotatePresenter mPresenter;

    QMUITipDialog mTipDialog;
    OperateUtils mOperateUtils;
    boolean mIsFirst = true;
    boolean mIsChanged;
    Bitmap mBitmap;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_rotate;
    }

    @Override
    protected void initViews() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
        }

        mBackAction.setText(R.string.back);
    }

    @OnClick({R.id.left_right, R.id.vertical_horizontal,
            R.id.horizontal_vertical, R.id.action_open, R.id.top_down})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_right:
                mIsChanged = true;
                mBitmap = PhotoUtils.reverseImage(mBitmap, -1, 1);
                mPicture.setImageBitmap(mBitmap);
                break;
            case R.id.vertical_horizontal:
                mIsChanged = true;
                mBitmap = PhotoUtils.rotateImage(mBitmap, 90);
                mPicture.setImageBitmap(mBitmap);
                break;
            case R.id.horizontal_vertical:
                mIsChanged = true;
                mBitmap = PhotoUtils.rotateImage(mBitmap, -90);
                mPicture.setImageBitmap(mBitmap);
                break;
            case R.id.action_open:
                onBackPressed();
                break;
            case R.id.top_down:
                mIsChanged = true;
                mBitmap = PhotoUtils.reverseImage(mBitmap, 1, -1);
                mPicture.setImageBitmap(mBitmap);
                break;
        }
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
                .applicationComponent(applicationComponent)
                .activityModule(new ActivityModule(this))
                .build()
                .inject(this);
    }

    @Override
    public void showImage(Bitmap bitmap) {
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

    private void back() {
        Intent i = new Intent(this, EditorActivity.class);
        i.putExtra("lastBitmap", mPresenter.getPath());
        setResult(Constant.REQUEST_EDIT_PHOTO_CODE, i);
        finish();
    }
}
