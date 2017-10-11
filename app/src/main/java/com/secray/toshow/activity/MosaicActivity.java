package com.secray.toshow.activity;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.secray.toshow.R;
import com.secray.toshow.Utils.Constant;
import com.secray.toshow.di.component.ApplicationComponent;
import com.secray.toshow.di.component.DaggerActivityComponent;
import com.secray.toshow.di.module.ActivityModule;
import com.secray.toshow.mvp.contract.MosaicContract;
import com.secray.toshow.mvp.presenter.MosaicPresenter;
import com.secray.toshow.widget.QMUITipDialog;

import javax.inject.Inject;

import butterknife.BindView;
import cn.jarlen.photoedit.mosaic.DrawMosaicView;
import cn.jarlen.photoedit.mosaic.MosaicUtil;
import cn.jarlen.photoedit.operate.OperateUtils;

/**
 * Created by user on 2017/9/22 0022.
 */

public class MosaicActivity extends BaseActivity implements MosaicContract.View, View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.mosaic)
    DrawMosaicView mMosaicView;
    @BindView(R.id.action_open)
    TextView mBackAction;

    @Inject
    MosaicPresenter mPresenter;

    QMUITipDialog mTipDialog;
    OperateUtils mOperateUtils;
    boolean mIsFirst = true;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_mosaic;
    }

    @Override
    protected void initViews() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
        }
        mBackAction.setText(R.string.back);
        mBackAction.setOnClickListener(this);
    }

    @Override
    protected void onWork() {
        mPresenter.bindView(this);
        mOperateUtils = new OperateUtils(this);
        String path = getIntent().getStringExtra("path");
        mPresenter.generateBitmap(path, mMosaicView, mOperateUtils);
    }

    @Override
    protected void setupActivityComponent(ApplicationComponent applicationComponent) {
        DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(applicationComponent)
                .build()
                .inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_done:
                if (mMosaicView.isChanged()) {
                    mTipDialog = new QMUITipDialog.Builder(this)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                            .setTipWord(getString(R.string.progress_dialog_saving))
                            .create();
                    mTipDialog.show();
                    mPresenter.savePic(mMosaicView.getMosaicBitmap());
                } else {
                    mTipDialog = new QMUITipDialog.Builder(this)
                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_INFO)
                            .setTipWord(getString(R.string.pic_not_changed_message))
                            .create();
                    mTipDialog.show();
                    mMosaicView.postDelayed(() -> mTipDialog.dismiss(), 1000);
                }
                break;
            case R.id.menu_clear:
                mMosaicView.clear();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showImage(Bitmap bitmap) {
        mMosaicView.setMosaicBackgroundResource(bitmap);
        Bitmap bit = MosaicUtil.getMosaic(bitmap);
        mMosaicView.setMosaicResource(bit);
        mMosaicView.setMosaicBrushWidth(10);
        if (mIsFirst) {
            ObjectAnimator alpha = ObjectAnimator.ofFloat(mMosaicView, "alpha", 0f, 1f);
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
        mMosaicView.postDelayed(() -> dialog.dismiss(), 1000);
        mPresenter.loadLastBitmap();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_open:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mMosaicView.isChanged()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.exit_message)
                    .setNegativeButton(android.R.string.ok, (dialog, which) -> back())
                    .setPositiveButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
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
